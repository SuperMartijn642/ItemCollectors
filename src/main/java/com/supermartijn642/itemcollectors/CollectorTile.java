package com.supermartijn642.itemcollectors;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class CollectorTile extends TileEntity implements ITickable {

    private static final int MIN_RANGE = 1;
    private static final int BASIC_MAX_RANGE = 5, BASIC_DEFAULT_RANGE = 3;
    private static final int ADVANCED_MAX_RANGE = 7, ADVANCED_DEFAULT_RANGE = 5;

    public static class BasicCollectorTile extends CollectorTile {
        public BasicCollectorTile(){
            super(BASIC_MAX_RANGE, BASIC_DEFAULT_RANGE, false);
        }
    }

    public static class AdvancedCollectorTile extends CollectorTile {
        public AdvancedCollectorTile(){
            super(ADVANCED_MAX_RANGE, ADVANCED_DEFAULT_RANGE, true);
        }
    }

    public static CollectorTile basicTile(){
        return new BasicCollectorTile();
    }

    public static CollectorTile advancedTile(){
        return new AdvancedCollectorTile();
    }

    private final int maxRange;
    private final boolean hasFilter;

    public int rangeX, rangeY, rangeZ;
    public final List<ItemStack> filter = new ArrayList<>(9);
    public boolean filterWhitelist;
    public boolean filterDurability = true;
    private boolean dataChanged;

    public CollectorTile(int maxRange, int range, boolean hasFilter){
        super();
        this.maxRange = maxRange;
        this.rangeX = this.rangeY = this.rangeZ = range;
        this.hasFilter = hasFilter;
        for(int i = 0; i < 9; i++)
            this.filter.add(ItemStack.EMPTY);
    }

    @Override
    public void update(){
        this.getOutputItemHandler().ifPresent(itemHandler -> {
            if(itemHandler.getSlots() <= 0)
                return;

            AxisAlignedBB area = new AxisAlignedBB(this.pos.add(-this.rangeX, -this.rangeY, -this.rangeZ), this.pos.add(this.rangeX + 1, this.rangeY + 1, this.rangeZ + 1));

            List<EntityItem> items = this.world.getEntitiesWithinAABB(EntityItem.class, area, item -> {
                if(item.getEntityData().hasKey("PreventRemoteMovement") && !item.getEntityData().hasKey("AllowMachineRemoteMovement"))
                    return false;
                if(!this.hasFilter)
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
                ItemStack stack = entity.getItem();
                for(int slot = 0; slot < itemHandler.getSlots(); slot++)
                    if(itemHandler.isItemValid(slot, stack)){
                        stack = itemHandler.insertItem(slot, stack, false);
                        if(stack.isEmpty()){
                            entity.setItem(ItemStack.EMPTY);
                            continue loop;
                        }
                    }
                entity.setItem(stack);
            }
        });
    }

    private Optional<IItemHandler> getOutputItemHandler(){
        TileEntity tile = this.world.getTileEntity(this.pos.down());
        if(tile == null || !tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP))
            return Optional.empty();
        return Optional.ofNullable(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP));
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
            IBlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(this.pos, state, state, 2);
        }
    }

    private NBTTagCompound getChangedData(){
        return this.dataChanged ? this.getData() : null;
    }

    private NBTTagCompound getData(){
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("rangeX", this.rangeX);
        tag.setInteger("rangeY", this.rangeY);
        tag.setInteger("rangeZ", this.rangeZ);
        if(this.hasFilter){
            for(int i = 0; i < 9; i++){
                if(!this.filter.get(i).isEmpty())
                    tag.setTag("filter" + i, this.filter.get(i).writeToNBT(new NBTTagCompound()));
            }
            tag.setBoolean("filterWhitelist", this.filterWhitelist);
            tag.setBoolean("filterDurability", this.filterDurability);
        }
        return tag;
    }

    private void handleData(NBTTagCompound tag){
        if(tag.hasKey("rangeX"))
            this.rangeX = tag.getInteger("rangeX");
        if(tag.hasKey("rangeY"))
            this.rangeY = tag.getInteger("rangeY");
        if(tag.hasKey("rangeZ"))
            this.rangeZ = tag.getInteger("rangeZ");
        if(this.hasFilter){
            for(int i = 0; i < 9; i++)
                this.filter.set(i, tag.hasKey("filter" + i) ? new ItemStack(tag.getCompoundTag("filter" + i)) : ItemStack.EMPTY);
            this.filterWhitelist = tag.hasKey("filterWhitelist") && tag.getBoolean("filterWhitelist");
            this.filterDurability = tag.hasKey("filterDurability") && tag.getBoolean("filterDurability");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound){
        super.writeToNBT(compound);
        compound.setTag("data", this.getData());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound){
        super.readFromNBT(compound);
        if(compound.hasKey("data"))
            this.handleData(compound.getCompoundTag("data"));
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket(){
        NBTTagCompound tag = this.getChangedData();
        return tag == null || tag.hasNoTags() ? null : new SPacketUpdateTileEntity(this.pos, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt){
        this.handleData(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag(){
        NBTTagCompound tag = super.getUpdateTag();
        tag.setTag("data", this.getData());
        return tag;
    }
}
