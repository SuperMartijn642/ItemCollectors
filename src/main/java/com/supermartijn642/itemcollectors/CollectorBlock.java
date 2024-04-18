package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.*;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

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
    protected InteractionFeedback interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, Direction hitSide, Vec3 hitLocation){
        if(level.isClientSide && !this.hasFilter.get())
            ItemCollectorsClient.openBasicCollectorScreen(level, pos);
        else if(!level.isClientSide && this.hasFilter.get())
            CommonUtils.openContainer(new AdvancedCollectorContainer(ItemCollectors.filter_collector_container, player, level, pos));
        return InteractionFeedback.SUCCESS;
    }

    @Override
    public BlockEntity createNewBlockEntity(BlockPos pos, BlockState state){
        return this.entityType.get().create(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context){
        return SHAPES[state.getValue(DIRECTION).get3DDataValue()].getUnderlying();
    }

    @Override
    protected void appendItemInformation(ItemStack stack, @org.jetbrains.annotations.Nullable BlockGetter level, Consumer<Component> info, boolean advanced){
        info.accept(TextComponents.translation("itemcollectors." + (this.hasFilter.get() ? "advanced" : "basic") + "_collector.info").color(ChatFormatting.AQUA).get());
        info.accept(TextComponents.translation("itemcollectors.basic_collector.info.range", TextComponents.number(this.maxRange.get()).color(ChatFormatting.GOLD).get()).get());
        super.appendItemInformation(stack, level, info, advanced);
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
