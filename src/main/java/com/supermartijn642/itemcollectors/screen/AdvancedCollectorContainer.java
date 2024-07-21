package com.supermartijn642.itemcollectors.screen;

import com.supermartijn642.core.gui.BaseContainerType;
import com.supermartijn642.core.gui.BlockEntityBaseContainer;
import com.supermartijn642.itemcollectors.CollectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class AdvancedCollectorContainer extends BlockEntityBaseContainer<CollectorBlockEntity> {

    public AdvancedCollectorContainer(BaseContainerType<?> containerType, Player player, Level level, BlockPos pos){
        super(containerType, player, level, pos);
        this.addSlots();
    }

    @Override
    protected void addSlots(Player player, CollectorBlockEntity entity){
        for(int i = 0; i < 9; i++)
            this.addSlot(new DummySlot(i, 8 + i * 18, 90) {
                @Override
                public ItemStack getItem(){
                    return AdvancedCollectorContainer.this.object.filter.get(this.index);
                }

                @Override
                public boolean mayPickup(Player player){
                    return false;
                }
            });
        this.addPlayerSlots(32, 124);
    }

    @Override
    public void clicked(int slot, int dragType, ClickType clickType, Player player){
        if(slot >= 0 && slot < 9){
            if(this.getCarried().isEmpty())
                this.object.filter.set(slot, ItemStack.EMPTY);
            else{
                ItemStack stack = this.getCarried().copy();
                stack.setCount(1);
                this.object.filter.set(slot, stack);
            }
            return;
        }
        super.clicked(slot, dragType, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index){
        if(index >= 0 && index < 9){
            if(this.getCarried().isEmpty())
                this.object.filter.set(index, ItemStack.EMPTY);
            else{
                ItemStack stack = this.getCarried().copy();
                stack.setCount(1);
                this.object.filter.set(index, stack);
            }
        }else if(!this.getSlot(index).getItem().isEmpty()){
            boolean contains = false;
            int firstEmpty = -1;
            for(int i = 0; i < 9; i++){
                ItemStack stack = this.object.filter.get(i);
                if(ItemStack.isSameItemSameComponents(stack, this.getSlot(index).getItem())){
                    contains = true;
                    break;
                }
                if(stack.isEmpty() && firstEmpty == -1)
                    firstEmpty = i;
            }
            if(!contains && firstEmpty != -1){
                ItemStack stack = this.getSlot(index).getItem().copy();
                stack.setCount(1);
                this.object.filter.set(firstEmpty, stack);
            }
        }
        return ItemStack.EMPTY;
    }

    public BlockPos getCollectorPosition(){
        return this.blockEntityPos;
    }
}
