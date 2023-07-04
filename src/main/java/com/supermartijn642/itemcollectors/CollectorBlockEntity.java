package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.BaseBlockEntityType;
import com.supermartijn642.core.block.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class CollectorBlockEntity extends BaseBlockEntity implements TickableBlockEntity {

    private static final int MIN_RANGE = 1;

    public static CollectorBlockEntity basicCollectorEntity(BlockPos pos, BlockState state){
        return new CollectorBlockEntity(ItemCollectors.basic_collector_tile, pos, state, ItemCollectorsConfig.basicCollectorMaxRange, ItemCollectorsConfig.basicCollectorFilter);
    }

    public static CollectorBlockEntity advancedCollectorEntity(BlockPos pos, BlockState state){
        return new CollectorBlockEntity(ItemCollectors.advanced_collector_tile, pos, state, ItemCollectorsConfig.advancedCollectorMaxRange, ItemCollectorsConfig.advancedCollectorFilter);
    }

    private final Supplier<Integer> maxRange;
    private final Supplier<Boolean> hasFilter;

    public int rangeX, rangeY, rangeZ;
    public final List<ItemStack> filter = new ArrayList<>(9);
    public boolean filterWhitelist;
    public boolean filterDurability = true;
    public boolean showArea = false;

    public CollectorBlockEntity(BaseBlockEntityType<CollectorBlockEntity> blockEntityType, BlockPos pos, BlockState state, Supplier<Integer> maxRange, Supplier<Boolean> hasFilter){
        super(blockEntityType, pos, state);
        this.maxRange = maxRange;
        this.rangeX = this.rangeY = this.rangeZ = (int)Math.ceil(maxRange.get() / 2f);
        this.hasFilter = hasFilter;
        for(int i = 0; i < 9; i++)
            this.filter.add(ItemStack.EMPTY);
    }

    @Override
    public void update(){
        if(!this.level.isClientSide){
            this.getOutputItemHandler().ifPresent(itemHandler -> {
                if(itemHandler.getSlots() <= 0)
                    return;

                AABB area = this.getAffectedArea();

                List<ItemEntity> items = this.level.getEntitiesOfClass(ItemEntity.class, area, item -> {
                    if(!item.isAlive() || (item.getPersistentData().contains("PreventRemoteMovement") && !item.getPersistentData().contains("AllowMachineRemoteMovement")))
                        return false;
                    if(!this.hasFilter.get())
                        return true;
                    ItemStack stack = item.getItem();
                    if(stack.isEmpty())
                        return false;
                    for(int i = 0; i < 9; i++){
                        ItemStack filter = this.filter.get(i);
                        if(ItemStack.isSameItem(filter, stack) &&
                            (!this.filterDurability || ItemStack.isSameItemSameTags(filter, stack)))
                            return this.filterWhitelist;
                    }
                    return !this.filterWhitelist;
                });

                loop:
                for(ItemEntity entity : items){
                    ItemStack stack = entity.getItem().copy();
                    for(int slot = 0; slot < itemHandler.getSlots(); slot++)
                        if(itemHandler.isItemValid(slot, stack)){
                            stack = itemHandler.insertItem(slot, stack, false);
                            if(stack.isEmpty()){
                                entity.setItem(ItemStack.EMPTY);
                                entity.remove(Entity.RemovalReason.DISCARDED);
                                continue loop;
                            }
                        }
                    entity.setItem(stack);
                }
            });
        }
    }

    public AABB getAffectedArea(){
        return new AABB(this.worldPosition.offset(-this.rangeX, -this.rangeY, -this.rangeZ), this.worldPosition.offset(this.rangeX + 1, this.rangeY + 1, this.rangeZ + 1));
    }

    private LazyOptional<IItemHandler> getOutputItemHandler(){
        BlockState state = this.getBlockState();
        if(!state.hasProperty(CollectorBlock.DIRECTION))
            return LazyOptional.empty();
        Direction direction = state.getValue(CollectorBlock.DIRECTION);
        BlockEntity entity = this.level.getBlockEntity(this.worldPosition.relative(direction));
        if(entity == null)
            return LazyOptional.empty();
        return entity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite());
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
    protected CompoundTag writeData(){
        CompoundTag tag = new CompoundTag();
        tag.putInt("rangeX", this.rangeX);
        tag.putInt("rangeY", this.rangeY);
        tag.putInt("rangeZ", this.rangeZ);
        for(int i = 0; i < 9; i++){
            if(!this.filter.get(i).isEmpty())
                tag.put("filter" + i, this.filter.get(i).save(new CompoundTag()));
        }
        tag.putBoolean("filterWhitelist", this.filterWhitelist);
        tag.putBoolean("filterDurability", this.filterDurability);
        tag.putBoolean("showArea", this.showArea);
        return tag;
    }

    @Override
    protected void readData(CompoundTag tag){
        if(tag.contains("rangeX"))
            this.rangeX = tag.getInt("rangeX");
        if(tag.contains("rangeY"))
            this.rangeY = tag.getInt("rangeY");
        if(tag.contains("rangeZ"))
            this.rangeZ = tag.getInt("rangeZ");
        for(int i = 0; i < 9; i++)
            this.filter.set(i, tag.contains("filter" + i) ? ItemStack.of(tag.getCompound("filter" + i)) : ItemStack.EMPTY);
        this.filterWhitelist = tag.contains("filterWhitelist") && tag.getBoolean("filterWhitelist");
        this.filterDurability = tag.contains("filterDurability") && tag.getBoolean("filterDurability");
        this.showArea = tag.contains("showArea") && tag.getBoolean("showArea");
    }

    @Override
    public AABB getRenderBoundingBox(){
        return this.getAffectedArea();
    }
}
