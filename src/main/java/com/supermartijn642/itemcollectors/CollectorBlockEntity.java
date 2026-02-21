package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.BaseBlockEntityType;
import com.supermartijn642.core.block.TickableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
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

    private int rangeX, rangeY, rangeZ;
    private final List<ItemStack> filter = new ArrayList<>(9);
    private boolean filterWhitelist;
    private boolean filterDurability = true;
    private boolean showArea = false;

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
        if(!this.level.isClientSide){
            this.getOutputItemHandler().ifPresent(itemHandler -> {
                if(itemHandler.getSlots() <= 0)
                    return;

                // Get a list of all items within range
                AxisAlignedBB area = this.getAffectedArea();
                List<ItemEntity> items = this.level.getEntitiesOfClass(ItemEntity.class, area);
                if(items.isEmpty())
                    return;

                // Try to insert up to the number of items defined in the config
                int maxInsertions = ItemCollectorsConfig.maxInsertions.get();
                if(maxInsertions <= 0)
                    maxInsertions = items.size();
                int remainingInsertions = maxInsertions;
                entityLoop:
                for(int i = 0; i < items.size() && remainingInsertions > 0 && i < maxInsertions * 10; i++){
                    ItemEntity entity = items.get(i);
                    // Filter dead item entities or entities which have the 'PreventRemoteMovement' tag
                    if(!entity.isAlive() || (entity.getPersistentData().contains("PreventRemoteMovement") && !entity.getPersistentData().contains("AllowMachineRemoteMovement")))
                        continue;
                    // Ignore entities with empty item stack
                    ItemStack stack = entity.getItem().copy();
                    if(stack.isEmpty())
                        continue;
                    // Compare item stack against filter
                    if(this.hasFilter.get()){
                        boolean matchesFilter = false;
                        for(int slot = 0; slot < 9; slot++){
                            ItemStack filter = this.filter.get(slot);
                            if(!filter.isEmpty() && ItemStack.isSame(filter, stack) &&
                                (!this.filterDurability || ItemStack.tagMatches(filter, stack))){
                                matchesFilter = true;
                                break;
                            }
                        }
                        if(matchesFilter != this.filterWhitelist)
                            continue;
                    }
                    // Try to insert the stack into storage
                    remainingInsertions--;
                    for(int slot = 0; slot < itemHandler.getSlots(); slot++){
                        if(itemHandler.isItemValid(slot, stack)){
                            stack = itemHandler.insertItem(slot, stack, false);
                            if(stack.isEmpty()){
                                entity.setItem(ItemStack.EMPTY);
                                entity.remove();
                                continue entityLoop;
                            }
                        }
                    }
                    entity.setItem(stack);
                }
            });
        }
    }

    public AxisAlignedBB getAffectedArea(){
        return new AxisAlignedBB(this.worldPosition.offset(-this.rangeX, -this.rangeY, -this.rangeZ), this.worldPosition.offset(this.rangeX + 1, this.rangeY + 1, this.rangeZ + 1));
    }

    private LazyOptional<IItemHandler> getOutputItemHandler(){
        BlockState state = this.getBlockState();
        if(!state.hasProperty(CollectorBlock.DIRECTION))
            return LazyOptional.empty();
        Direction direction = state.getValue(CollectorBlock.DIRECTION);
        TileEntity entity = this.level.getBlockEntity(this.worldPosition.relative(direction));
        if(entity == null)
            return LazyOptional.empty();
        return entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite());
    }

    public int getRangeX(){
        return this.rangeX;
    }

    public void increaseRangeX(int amount){
        int old = this.rangeX;
        this.rangeX = Math.min(Math.max(this.rangeX + amount, MIN_RANGE), this.maxRange.get());
        if(this.rangeX != old)
            this.dataChanged();
    }

    public int getRangeY(){
        return this.rangeY;
    }

    public void increaseRangeY(int amount){
        int old = this.rangeY;
        this.rangeY = Math.min(Math.max(this.rangeY + amount, MIN_RANGE), this.maxRange.get());
        if(this.rangeY != old)
            this.dataChanged();
    }

    public int getRangeZ(){
        return this.rangeZ;
    }

    public void increaseRangeZ(int amount){
        int old = this.rangeZ;
        this.rangeZ = Math.min(Math.max(this.rangeZ + amount, MIN_RANGE), this.maxRange.get());
        if(this.rangeZ != old)
            this.dataChanged();
    }

    public ItemStack getFilterStack(int index){
        return this.filter.get(index);
    }

    public void setFilterStack(int index, ItemStack stack){
        this.filter.set(index, stack);
        this.dataChanged();
    }

    public boolean getFilterWhitelist(){
        return this.filterWhitelist;
    }

    public void toggleFilterWhitelist(){
        this.filterWhitelist = !this.filterWhitelist;
        this.dataChanged();
    }

    public boolean getFilterDurability(){
        return this.filterDurability;
    }

    public void toggleFilterDurability(){
        this.filterDurability = !this.filterDurability;
        this.dataChanged();
    }

    public boolean shouldShowArea(){
        return this.showArea;
    }

    public void toggleShowArea(){
        this.showArea = !this.showArea;
        this.dataChanged();
    }

    @Override
    protected CompoundNBT writeData(){
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("rangeX", this.rangeX);
        tag.putInt("rangeY", this.rangeY);
        tag.putInt("rangeZ", this.rangeZ);
        for(int i = 0; i < 9; i++){
            if(!this.filter.get(i).isEmpty())
                tag.put("filter" + i, this.filter.get(i).save(new CompoundNBT()));
        }
        tag.putBoolean("filterWhitelist", this.filterWhitelist);
        tag.putBoolean("filterDurability", this.filterDurability);
        tag.putBoolean("showArea", this.showArea);
        return tag;
    }

    @Override
    protected void readData(CompoundNBT tag){
        this.rangeX = tag.contains("rangeX") ? tag.getInt("rangeX") : MIN_RANGE;
        this.rangeY = tag.contains("rangeY") ? tag.getInt("rangeY") : MIN_RANGE;
        this.rangeZ = tag.contains("rangeZ") ? tag.getInt("rangeZ") : MIN_RANGE;
        for(int i = 0; i < 9; i++)
            this.filter.set(i, tag.contains("filter" + i) ? ItemStack.of(tag.getCompound("filter" + i)) : ItemStack.EMPTY);
        this.filterWhitelist = tag.contains("filterWhitelist") && tag.getBoolean("filterWhitelist");
        this.filterDurability = tag.contains("filterDurability") && tag.getBoolean("filterDurability");
        this.showArea = tag.contains("showArea") && tag.getBoolean("showArea");
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox(){
        return this.getAffectedArea();
    }
}
