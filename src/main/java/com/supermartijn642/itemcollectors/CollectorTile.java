package com.supermartijn642.itemcollectors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class CollectorTile extends TileEntity implements ITickableTileEntity {

    private static final int MIN_RANGE = 1;
    public static final int BASIC_MAX_RANGE = 5, BASIC_DEFAULT_RANGE = 3;
    public static final int ADVANCED_MAX_RANGE = 7, ADVANCED_DEFAULT_RANGE = 5;

    public static CollectorTile basicTile(){
        return new CollectorTile(ItemCollectors.basic_collector_tile, BASIC_MAX_RANGE, BASIC_DEFAULT_RANGE, false);
    }

    public static CollectorTile advancedTile(){
        return new CollectorTile(ItemCollectors.advanced_collector_tile, ADVANCED_MAX_RANGE, ADVANCED_DEFAULT_RANGE, true);
    }

    private final int maxRange;
    private final boolean hasFilter;

    public int rangeX, rangeY, rangeZ;
    public final List<ItemStack> filter = new ArrayList<>(9);
    public boolean filterWhitelist;
    public boolean filterDurability = true;
    private boolean dataChanged;

    public CollectorTile(TileEntityType<CollectorTile> tileEntityType, int maxRange, int range, boolean hasFilter){
        super(tileEntityType);
        this.maxRange = maxRange;
        this.rangeX = this.rangeY = this.rangeZ = range;
        this.hasFilter = hasFilter;
        for(int i = 0; i < 9; i++)
            this.filter.add(ItemStack.EMPTY);
    }

    @Override
    public void tick(){
        this.getOutputItemHandler().ifPresent(itemHandler -> {
            if(itemHandler.getSlots() <= 0)
                return;

            AxisAlignedBB area = new AxisAlignedBB(this.pos.add(-this.rangeX, -this.rangeY, -this.rangeZ), this.pos.add(this.rangeX + 1, this.rangeY + 1, this.rangeZ + 1));

            List<Entity> items = this.world.getEntitiesWithinAABB(EntityType.ITEM, area, item -> {
                if(!(item instanceof ItemEntity) ||
                    (item.getPersistentData().contains("PreventRemoteMovement") && !item.getPersistentData().contains("AllowMachineRemoteMovement")))
                    return false;
                if(!this.hasFilter)
                    return true;
                ItemStack stack = ((ItemEntity)item).getItem();
                if(stack.isEmpty())
                    return false;
                for(int i = 0; i < 9; i++){
                    ItemStack filter = this.filter.get(i);
                    if(ItemStack.areItemsEqual(filter, stack) &&
                        (!this.filterDurability || ItemStack.areItemStackTagsEqual(filter, stack)))
                        return this.filterWhitelist;
                }
                return !this.filterWhitelist;
            });

            loop:
            for(Entity entity : items){
                ItemStack stack = ((ItemEntity)entity).getItem();
                for(int slot = 0; slot < itemHandler.getSlots(); slot++)
                    if(itemHandler.isItemValid(slot, stack)){
                        stack = itemHandler.insertItem(slot, stack, false);
                        if(stack.isEmpty()){
                            ((ItemEntity)entity).setItem(ItemStack.EMPTY);
                            continue loop;
                        }
                    }
                ((ItemEntity)entity).setItem(stack);
            }
        });
    }

    private LazyOptional<IItemHandler> getOutputItemHandler(){
        TileEntity tile = this.world.getTileEntity(this.pos.down());
        if(tile == null)
            return LazyOptional.empty();
        return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
    }

    public void setRangeX(int range){
        int old = this.rangeX;
        this.rangeX = Math.min(Math.max(range, MIN_RANGE), this.maxRange);
        if(this.rangeX != old)
            this.dataChanged();
    }

    public void setRangeY(int range){
        int old = this.rangeY;
        this.rangeY = Math.min(Math.max(range, MIN_RANGE), this.maxRange);
        if(this.rangeY != old)
            this.dataChanged();
    }

    public void setRangeZ(int range){
        int old = this.rangeZ;
        this.rangeZ = Math.min(Math.max(range, MIN_RANGE), this.maxRange);
        if(this.rangeZ != old)
            this.dataChanged();
    }

    public void dataChanged(){
        if(!this.world.isRemote){
            this.dataChanged = true;
            this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), 2);
        }
    }

    private CompoundNBT getChangedData(){
        return this.dataChanged ? this.getData() : null;
    }

    private CompoundNBT getData(){
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("rangeX", this.rangeX);
        tag.putInt("rangeY", this.rangeY);
        tag.putInt("rangeZ", this.rangeZ);
        if(this.hasFilter){
            for(int i = 0; i < 9; i++){
                if(!this.filter.get(i).isEmpty())
                    tag.put("filter" + i, this.filter.get(i).write(new CompoundNBT()));
            }
            tag.putBoolean("filterWhitelist", this.filterWhitelist);
            tag.putBoolean("filterDurability", this.filterDurability);
        }
        return tag;
    }

    private void handleData(CompoundNBT tag){
        if(tag.contains("rangeX"))
            this.rangeX = tag.getInt("rangeX");
        if(tag.contains("rangeY"))
            this.rangeY = tag.getInt("rangeY");
        if(tag.contains("rangeZ"))
            this.rangeZ = tag.getInt("rangeZ");
        if(this.hasFilter){
            for(int i = 0; i < 9; i++)
                this.filter.set(i, tag.contains("filter" + i) ? ItemStack.read(tag.getCompound("filter" + i)) : ItemStack.EMPTY);
            this.filterWhitelist = tag.contains("filterWhitelist") && tag.getBoolean("filterWhitelist");
            this.filterDurability = tag.contains("filterDurability") && tag.getBoolean("filterDurability");
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound){
        super.write(compound);
        compound.put("data", this.getData());
        return compound;
    }

    @Override
    public void read(CompoundNBT compound){
        super.read(compound);
        if(compound.contains("data"))
            this.handleData(compound.getCompound("data"));
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket(){
        CompoundNBT tag = this.getChangedData();
        return tag == null || tag.isEmpty() ? null : new SUpdateTileEntityPacket(this.pos, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
        this.handleData(pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag(){
        CompoundNBT tag = super.getUpdateTag();
        tag.put("data", this.getData());
        return tag;
    }
}
