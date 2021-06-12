package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.ToolType;
import com.supermartijn642.core.block.BaseBlock;
import com.supermartijn642.core.block.BlockShape;
import com.supermartijn642.itemcollectors.screen.BasicCollectorScreen;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class CollectorBlock extends BaseBlock {

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

    private final Supplier<CollectorTile> tileSupplier;
    private final Supplier<Integer> maxRange;
    private final Supplier<Boolean> hasFilter;

    public CollectorBlock(String registryName, Supplier<CollectorTile> tileSupplier, Supplier<Integer> maxRange, Supplier<Boolean> hasFilter){
        super(registryName, false, Properties.create(Material.ROCK, MapColor.BLACK).harvestTool(ToolType.PICKAXE).harvestLevel(1).hardnessAndResistance(5, 1200));
        this.tileSupplier = tileSupplier;
        this.maxRange = maxRange;
        this.hasFilter = hasFilter;
        this.setCreativeTab(CreativeTabs.SEARCH);
        this.setDefaultState(this.getDefaultState().withProperty(DIRECTION, EnumFacing.DOWN));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand handIn, EnumFacing facing, float hitX, float hitY, float hitZ){
        if(worldIn.isRemote && !this.hasFilter.get())
            ClientUtils.displayScreen(new BasicCollectorScreen(pos));
        else if(!worldIn.isRemote && this.hasFilter.get())
            player.openGui(ItemCollectors.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face){
        return face == state.getValue(DIRECTION) ? BlockFaceShape.CENTER : BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean hasTileEntity(IBlockState state){
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state){
        return this.tileSupplier.get();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state){
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced){
        tooltip.add(new TextComponentTranslation("itemcollectors." + (this.hasFilter.get() ? "advanced" : "basic") + "_collector.info", this.maxRange.get()).setStyle(new Style().setColor(TextFormatting.AQUA)).getFormattedText());
        super.addInformation(stack, player, tooltip, advanced);
    }

    @Override
    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, DIRECTION);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand){
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
