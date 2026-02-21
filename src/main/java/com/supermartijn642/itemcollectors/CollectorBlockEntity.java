package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.BaseBlockEntityType;
import com.supermartijn642.core.block.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

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

    private int rangeX, rangeY, rangeZ;
    private final List<ItemStack> filter = new ArrayList<>(9);
    private boolean filterWhitelist;
    private boolean filterDurability = true;
    private boolean showArea = false;

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
        if(!this.level.isClientSide()){
            ResourceHandler<ItemResource> itemHandler = this.getOutputItemHandler();
            if(itemHandler != null && itemHandler.size() > 0){
                // Get a list of all items within range
                AABB area = this.getAffectedArea();
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
                            if(!filter.isEmpty() && ItemStack.isSameItem(filter, stack) &&
                                (!this.filterDurability || ItemStack.isSameItemSameComponents(filter, stack))){
                                matchesFilter = true;
                                break;
                            }
                        }
                        if(matchesFilter != this.filterWhitelist)
                            continue;
                    }
                    // Try to insert the stack into storage
                    remainingInsertions--;
                    try(Transaction transaction = Transaction.openRoot()){
                        int inserted = itemHandler.insert(ItemResource.of(stack), stack.getCount(), transaction);
                        if(inserted > 0){
                            stack.shrink(inserted);
                            entity.setItem(stack);
                            if(stack.isEmpty())
                                entity.remove(Entity.RemovalReason.DISCARDED);
                        }
                        transaction.commit();
                    }
                }
            }
        }
    }

    public AABB getAffectedArea(){
        return AABB.encapsulatingFullBlocks(this.worldPosition.offset(-this.rangeX, -this.rangeY, -this.rangeZ), this.worldPosition.offset(this.rangeX, this.rangeY, this.rangeZ));
    }

    private ResourceHandler<ItemResource> getOutputItemHandler(){
        BlockState state = this.getBlockState();
        if(!state.hasProperty(CollectorBlock.DIRECTION))
            return null;
        Direction direction = state.getValue(CollectorBlock.DIRECTION);
        return this.level.getCapability(Capabilities.Item.BLOCK, this.worldPosition.relative(direction), direction.getOpposite());
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
    protected void writeData(ValueOutput output){
        output.putInt("rangeX", this.rangeX);
        output.putInt("rangeY", this.rangeY);
        output.putInt("rangeZ", this.rangeZ);
        for(int i = 0; i < 9; i++){
            if(!this.filter.get(i).isEmpty())
                output.store("filter" + i, ItemStack.CODEC, this.filter.get(i));
        }
        output.putBoolean("filterWhitelist", this.filterWhitelist);
        output.putBoolean("filterDurability", this.filterDurability);
        output.putBoolean("showArea", this.showArea);
    }

    @Override
    protected void readData(ValueInput input){
        this.rangeX = input.getIntOr("rangeX", MIN_RANGE);
        this.rangeY = input.getIntOr("rangeY", MIN_RANGE);
        this.rangeZ = input.getIntOr("rangeZ", MIN_RANGE);
        for(int i = 0; i < 9; i++)
            this.filter.set(i, input.read("filter" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY));
        this.filterWhitelist = input.getBooleanOr("filterWhitelist", false);
        this.filterDurability = input.getBooleanOr("filterDurability", false);
        this.showArea = input.getBooleanOr("showArea", false);
    }
}
