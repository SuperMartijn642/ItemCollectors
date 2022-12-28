package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.*;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorContainer;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class CollectorBlock extends BaseBlock implements EntityHoldingBlock {

    public static final PropertyEnum<EnumFacing> DIRECTION = BlockDirectional.FACING;
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
        SHAPES[EnumFacing.DOWN.getIndex()] = SHAPE;
        SHAPES[EnumFacing.UP.getIndex()] = SHAPE.rotate(EnumFacing.Axis.X).rotate(EnumFacing.Axis.X);
        SHAPES[EnumFacing.NORTH.getIndex()] = SHAPE.rotate(EnumFacing.Axis.X).rotate(EnumFacing.Axis.Y).rotate(EnumFacing.Axis.Y);
        SHAPES[EnumFacing.EAST.getIndex()] = SHAPE.rotate(EnumFacing.Axis.X).rotate(EnumFacing.Axis.Y).rotate(EnumFacing.Axis.Y).rotate(EnumFacing.Axis.Y);
        SHAPES[EnumFacing.SOUTH.getIndex()] = SHAPE.rotate(EnumFacing.Axis.X);
        SHAPES[EnumFacing.WEST.getIndex()] = SHAPE.rotate(EnumFacing.Axis.X).rotate(EnumFacing.Axis.Y);
    }

    private final Supplier<BaseBlockEntityType<?>> entityType;
    private final Supplier<Integer> maxRange;
    private final Supplier<Boolean> hasFilter;

    public CollectorBlock(Supplier<BaseBlockEntityType<?>> entityType, Supplier<Integer> maxRange, Supplier<Boolean> hasFilter){
        super(false, BlockProperties.create(Material.ROCK, MapColor.BLACK).requiresCorrectTool().destroyTime(5).explosionResistance(1200));
        this.entityType = entityType;
        this.maxRange = maxRange;
        this.hasFilter = hasFilter;
        this.setDefaultState(this.getDefaultState().withProperty(DIRECTION, EnumFacing.DOWN));
    }

    @Override
    protected InteractionFeedback interact(IBlockState state, World level, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing hitSide, Vec3d hitLocation){
        if(level.isRemote && !this.hasFilter.get())
            ItemCollectorsClient.openBasicCollectorScreen(level, pos);
        else if(!level.isRemote && this.hasFilter.get())
            CommonUtils.openContainer(new AdvancedCollectorContainer(ItemCollectors.filter_collector_container, player, level, pos));
        return super.interact(state, level, pos, player, hand, hitSide, hitLocation);
    }

    @Override
    public TileEntity createNewBlockEntity(){
        return this.entityType.get().createBlockEntity();
    }

    @Nonnull
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        return SHAPES[state.getValue(DIRECTION).getIndex()].simplify();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState){
        for(AxisAlignedBB box : SHAPES[state.getValue(DIRECTION).getIndex()].toBoxes())
            addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
    }

    @Override
    public boolean isFullCube(IBlockState state){
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess level, IBlockState state, BlockPos pos, EnumFacing face){
        return face == state.getValue(DIRECTION) ? BlockFaceShape.CENTER : BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state){
        return false;
    }

    @Override
    protected void appendItemInformation(ItemStack stack, @Nullable IBlockAccess level, Consumer<ITextComponent> info, boolean advanced){
        info.accept(TextComponents.translation("itemcollectors." + (this.hasFilter.get() ? "advanced" : "basic") + "_collector.info").color(TextFormatting.AQUA).get());
        info.accept(TextComponents.translation("itemcollectors.basic_collector.info.range", TextComponents.number(this.maxRange.get()).color(TextFormatting.GOLD).get()).get());
        super.appendItemInformation(stack, level, info, advanced);
    }

    @Override
    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, DIRECTION);
    }

    @Override
    public IBlockState getStateForPlacement(World level, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand){
        return this.getDefaultState().withProperty(DIRECTION, facing.getOpposite());
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return state.getValue(DIRECTION).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta){
        return this.getDefaultState().withProperty(DIRECTION, EnumFacing.getFront(meta));
    }
}
