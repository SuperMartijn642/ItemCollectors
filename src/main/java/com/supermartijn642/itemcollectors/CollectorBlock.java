package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.BaseBlock;
import com.supermartijn642.core.block.BlockShape;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class CollectorBlock extends BaseBlock implements EntityBlock {

    public static final EnumProperty<Direction> DIRECTION = BlockStateProperties.FACING;
    private static final BlockShape SHAPE = BlockShape.or(
        BlockShape.createBlockShape(3, 0, 3, 13, 1, 13),
        BlockShape.createBlockShape(5, 1, 5, 11, 2, 11),
        BlockShape.createBlockShape(6, 2, 6, 10, 5, 10),
        BlockShape.createBlockShape(5, 5, 5, 11, 6, 11),
        BlockShape.createBlockShape(5, 6, 5, 6, 11, 6),
        BlockShape.createBlockShape(5, 6, 10, 6, 11, 11),
        BlockShape.createBlockShape(10, 6, 5, 11, 11, 6),
        BlockShape.createBlockShape(10, 6, 10, 11, 11, 11),
        BlockShape.createBlockShape(6, 10, 5, 11, 11, 6),
        BlockShape.createBlockShape(6, 10, 10, 10, 11, 11),
        BlockShape.createBlockShape(5, 10, 6, 6, 11, 10),
        BlockShape.createBlockShape(10, 10, 6, 11, 11, 10),
        BlockShape.createBlockShape(6, 6, 6, 10, 10, 10));
    private static final BlockShape[] SHAPES = new BlockShape[6];

    static{
        SHAPES[Direction.DOWN.get3DDataValue()] = SHAPE;
        SHAPES[Direction.UP.get3DDataValue()] = SHAPE.rotate(Direction.Axis.X).rotate(Direction.Axis.X);
        SHAPES[Direction.NORTH.get3DDataValue()] = SHAPE.rotate(Direction.Axis.X).rotate(Direction.Axis.Y).rotate(Direction.Axis.Y);
        SHAPES[Direction.EAST.get3DDataValue()] = SHAPE.rotate(Direction.Axis.X).rotate(Direction.Axis.Y).rotate(Direction.Axis.Y).rotate(Direction.Axis.Y);
        SHAPES[Direction.SOUTH.get3DDataValue()] = SHAPE.rotate(Direction.Axis.X);
        SHAPES[Direction.WEST.get3DDataValue()] = SHAPE.rotate(Direction.Axis.X).rotate(Direction.Axis.Y);
    }

    private final BiFunction<BlockPos,BlockState,CollectorTile> tileSupplier;
    private final Supplier<Integer> maxRange;
    private final Supplier<Boolean> hasFilter;

    public CollectorBlock(String registryName, BiFunction<BlockPos,BlockState,CollectorTile> tileSupplier, Supplier<Integer> maxRange, Supplier<Boolean> hasFilter){
        super(registryName, false, Properties.of(Material.STONE, MaterialColor.COLOR_BLACK).harvestTool(ToolType.PICKAXE).harvestLevel(1).strength(5, 1200));
        this.tileSupplier = tileSupplier;
        this.maxRange = maxRange;
        this.hasFilter = hasFilter;
        this.registerDefaultState(this.defaultBlockState().setValue(DIRECTION, Direction.DOWN));
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult p_225533_6_){
        if(worldIn.isClientSide && !this.hasFilter.get())
            ClientProxy.openBasicCollectorScreen(pos);
        else if(!worldIn.isClientSide && this.hasFilter.get())
            NetworkHooks.openGui((ServerPlayer)player, new MenuProvider() {
                @Override
                public Component getDisplayName(){
                    return TextComponents.empty().get();
                }

                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player){
                    return new AdvancedCollectorContainer(id, player, pos);
                }
            }, pos);
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
        return this.tileSupplier.apply(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> blockEntityType){
        return blockEntityType.getRegistryName().getNamespace().equals("itemcollectors") ?
            (world2, pos, state2, entity) -> ((CollectorTile)entity).tick() : null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
        return SHAPES[state.getValue(DIRECTION).get3DDataValue()].getUnderlying();
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
        tooltip.add(TextComponents.translation("itemcollectors." + (this.hasFilter.get() ? "advanced" : "basic") + "_collector.info", this.maxRange.get()).color(ChatFormatting.AQUA).get());
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){
        builder.add(DIRECTION);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context){
        return this.defaultBlockState().setValue(DIRECTION, context.getClickedFace().getOpposite());
    }
}
