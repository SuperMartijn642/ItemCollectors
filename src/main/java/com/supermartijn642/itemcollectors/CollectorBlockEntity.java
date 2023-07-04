package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.BaseBlockEntityType;
import com.supermartijn642.core.block.TickableBlockEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class CollectorBlockEntity extends BaseBlockEntity implements TickableBlockEntity {

    private static final int MIN_RANGE = 1;

    public static CollectorBlockEntity basicCollectorEntity(){
        return new CollectorBlockEntity(ItemCollectors.basic_collector_tile, ItemCollectorsConfig.basicCollectorMaxRange, ItemCollectorsConfig.basicCollectorFilter);
    }

    public static CollectorBlockEntity advancedCollectorEntity(){
        return new CollectorBlockEntity(ItemCollectors.advanced_collector_tile, ItemCollectorsConfig.advancedCollectorMaxRange, ItemCollectorsConfig.advancedCollectorFilter);
    }

    private final Supplier<Integer> maxRange;
    private final Supplier<Boolean> hasFilter;

    public int rangeX, rangeY, rangeZ;
    public final List<ItemStack> filter = new ArrayList<>(9);
    public boolean filterWhitelist;
    public boolean filterDurability = true;
    public boolean showArea = false;

    public CollectorBlockEntity(BaseBlockEntityType<CollectorBlockEntity> blockEntityType, Supplier<Integer> maxRange, Supplier<Boolean> hasFilter){
        super(blockEntityType);
        this.maxRange = maxRange;
        this.rangeX = this.rangeY = this.rangeZ = (int)Math.ceil(maxRange.get() / 2f);
        this.hasFilter = hasFilter;
        for(int i = 0; i < 9; i++)
            this.filter.add(ItemStack.EMPTY);
    }

    @Override
    public void update(){
        if(!this.world.isRemote){
            this.getOutputItemHandler().ifPresent(itemHandler -> {
                if(itemHandler.getSlots() <= 0)
                    return;

                AxisAlignedBB area = this.getAffectedArea();

                List<EntityItem> items = this.world.getEntitiesWithinAABB(EntityItem.class, area, item -> {
                    if(!item.isEntityAlive() || (item.getEntityData().hasKey("PreventRemoteMovement") && !item.getEntityData().hasKey("AllowMachineRemoteMovement")))
                        return false;
                    if(!this.hasFilter.get())
                        return true;
                    ItemStack stack = item.getItem();
                    if(stack.isEmpty())
                        return false;
                    for(int i = 0; i < 9; i++){
                        ItemStack filter = this.filter.get(i);
                        if((this.filterDurability ? ItemStack.areItemsEqual(filter, stack) : ItemStack.areItemsEqualIgnoreDurability(filter, stack)) &&
                            ItemStack.areItemStackTagsEqual(filter, stack))
                            return this.filterWhitelist;
                    }
                    return !this.filterWhitelist;
                });

                loop:
                for(EntityItem entity : items){
                    ItemStack stack = entity.getItem().copy();
                    for(int slot = 0; slot < itemHandler.getSlots(); slot++)
                        if(itemHandler.isItemValid(slot, stack)){
                            stack = itemHandler.insertItem(slot, stack, false);
                            if(stack.isEmpty()){
                                entity.setItem(ItemStack.EMPTY);
                                entity.setDead();
                                continue loop;
                            }
                        }
                    entity.setItem(stack);
                }
            });
        }
    }

    public AxisAlignedBB getAffectedArea(){
        return new AxisAlignedBB(this.pos.add(-this.rangeX, -this.rangeY, -this.rangeZ), this.pos.add(this.rangeX + 1, this.rangeY + 1, this.rangeZ + 1));
    }

    private Optional<IItemHandler> getOutputItemHandler(){
        IBlockState state = this.getBlockState();
        if(!state.getPropertyKeys().contains(CollectorBlock.DIRECTION))
            return Optional.empty();
        EnumFacing direction = state.getValue(CollectorBlock.DIRECTION);
        TileEntity entity = this.world.getTileEntity(this.pos.offset(direction));
        if(entity == null || !entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()))
            return Optional.empty();
        return Optional.ofNullable(entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()));
    }

    public void setRangeX(int range){
        int old = this.rangeX;
        this.rangeX = Math.min(Math.max(range, MIN_RANGE), this.maxRange.get());
        if(this.rangeX != old)
            this.dataChanged();
    }

    public void setRangeY(int range){
        int old = this.rangeY;
        this.rangeY = Math.min(Math.max(range, MIN_RANGE), this.maxRange.get());
        if(this.rangeY != old)
            this.dataChanged();
    }

    public void setRangeZ(int range){
        int old = this.rangeZ;
        this.rangeZ = Math.min(Math.max(range, MIN_RANGE), this.maxRange.get());
        if(this.rangeZ != old)
            this.dataChanged();
    }

    public void setShowArea(boolean showArea){
        if(this.showArea != showArea){
            this.showArea = showArea;
            this.dataChanged();
        }
    }

    @Override
    protected NBTTagCompound writeData(){
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("rangeX", this.rangeX);
        tag.setInteger("rangeY", this.rangeY);
        tag.setInteger("rangeZ", this.rangeZ);
        for(int i = 0; i < 9; i++){
            if(!this.filter.get(i).isEmpty())
                tag.setTag("filter" + i, this.filter.get(i).writeToNBT(new NBTTagCompound()));
        }
        tag.setBoolean("filterWhitelist", this.filterWhitelist);
        tag.setBoolean("filterDurability", this.filterDurability);
        tag.setBoolean("showArea", this.showArea);
        return tag;
    }

    @Override
    protected void readData(NBTTagCompound tag){
        if(tag.hasKey("rangeX"))
            this.rangeX = tag.getInteger("rangeX");
        if(tag.hasKey("rangeY"))
            this.rangeY = tag.getInteger("rangeY");
        if(tag.hasKey("rangeZ"))
            this.rangeZ = tag.getInteger("rangeZ");
        for(int i = 0; i < 9; i++)
            this.filter.set(i, tag.hasKey("filter" + i) ? new ItemStack(tag.getCompoundTag("filter" + i)) : ItemStack.EMPTY);
        this.filterWhitelist = tag.hasKey("filterWhitelist") && tag.getBoolean("filterWhitelist");
        this.filterDurability = tag.hasKey("filterDurability") && tag.getBoolean("filterDurability");
        this.showArea = tag.hasKey("showArea") && tag.getBoolean("showArea");
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox(){
        return this.getAffectedArea();
    }
}
