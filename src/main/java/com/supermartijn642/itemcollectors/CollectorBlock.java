package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.*;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class CollectorBlock extends BaseBlock implements EntityHoldingBlock {

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

    private final Supplier<BaseBlockEntityType<?>> entityType;
    private final Supplier<Integer> maxRange;
    private final Supplier<Boolean> hasFilter;

    public CollectorBlock(Supplier<BaseBlockEntityType<?>> entityType, Supplier<Integer> maxRange, Supplier<Boolean> hasFilter){
        super(false, BlockProperties.create(Material.STONE, MaterialColor.COLOR_BLACK).requiresCorrectTool().destroyTime(5).explosionResistance(1200));
        this.entityType = entityType;
        this.maxRange = maxRange;
        this.hasFilter = hasFilter;
        this.registerDefaultState(this.defaultBlockState().setValue(DIRECTION, Direction.DOWN));
    }

    @Override
    protected InteractionFeedback interact(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, Direction hitSide, Vector3d hitLocation){
        if(level.isClientSide && !this.hasFilter.get())
            ItemCollectorsClient.openBasicCollectorScreen(level, pos);
        else if(!level.isClientSide && this.hasFilter.get())
            CommonUtils.openContainer(new AdvancedCollectorContainer(ItemCollectors.filter_collector_container, player, level, pos));
        return InteractionFeedback.SUCCESS;
    }

    @Override
    public TileEntity createNewBlockEntity(){
        return this.entityType.get().create();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context){
        return SHAPES[state.getValue(DIRECTION).get3DDataValue()].getUnderlying();
    }

    @Override
    protected void appendItemInformation(ItemStack stack, @Nullable IBlockReader level, Consumer<ITextComponent> info, boolean advanced){
        info.accept(TextComponents.translation("itemcollectors." + (this.hasFilter.get() ? "advanced" : "basic") + "_collector.info").color(TextFormatting.AQUA).get());
        info.accept(TextComponents.translation("itemcollectors.basic_collector.info.range", TextComponents.number(this.maxRange.get()).color(TextFormatting.GOLD).get()).get());
        super.appendItemInformation(stack, level, info, advanced);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block,BlockState> builder){
        builder.add(DIRECTION);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context){
        return this.defaultBlockState().setValue(DIRECTION, context.getClickedFace().getOpposite());
    }
}
