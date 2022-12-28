package com.supermartijn642.itemcollectors.screen;

import com.supermartijn642.core.gui.BaseContainerType;
import com.supermartijn642.core.gui.BlockEntityBaseContainer;
import com.supermartijn642.itemcollectors.CollectorBlockEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class AdvancedCollectorContainer extends BlockEntityBaseContainer<CollectorBlockEntity> {

    public AdvancedCollectorContainer(BaseContainerType<?> containerType, EntityPlayer player, World level, BlockPos pos){
        super(containerType, player, level, pos);
        this.addSlots();
    }

    @Override
    protected void addSlots(EntityPlayer player, CollectorBlockEntity entity){
        for(int i = 0; i < 9; i++)
            this.addSlot(new SlotItemHandler(this.itemHandler(), i, 8 + i * 18, 90) {
                @Override
                public boolean canTakeStack(EntityPlayer player){
                    return false;
                }
            });
        this.addPlayerSlots(32, 124);
    }

    @Override
    public ItemStack slotClick(int slot, int dragType, ClickType clickType, EntityPlayer player){
        if(slot >= 0 && slot < 9){
            if(this.player.inventory.getItemStack().isEmpty())
                this.object.filter.set(slot, ItemStack.EMPTY);
            else{
                ItemStack stack = this.player.inventory.getItemStack().copy();
                stack.setCount(1);
                this.object.filter.set(slot, stack);
            }
            return ItemStack.EMPTY;
        }
        return super.slotClick(slot, dragType, clickType, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index){
        if(index >= 0 && index < 9){
            if(this.player.inventory.getItemStack().isEmpty())
                this.object.filter.set(index, ItemStack.EMPTY);
            else{
                ItemStack stack = this.player.inventory.getItemStack().copy();
                stack.setCount(1);
                this.object.filter.set(index, stack);
            }
        }else if(!this.getSlot(index).getStack().isEmpty()){
            boolean contains = false;
            int firstEmpty = -1;
            for(int i = 0; i < 9; i++){
                ItemStack stack = this.itemHandler().getStackInSlot(i);
                if(ItemStack.areItemsEqual(stack, this.getSlot(index).getStack()) && ItemStack.areItemStackTagsEqual(stack, this.getSlot(index).getStack())){
                    contains = true;
                    break;
                }
                if(stack.isEmpty() && firstEmpty == -1)
                    firstEmpty = i;
            }
            if(!contains && firstEmpty != -1){
                ItemStack stack = this.getSlot(index).getStack().copy();
                stack.setCount(1);
                this.object.filter.set(firstEmpty, stack);
            }
        }
        return ItemStack.EMPTY;
    }

    private ItemStackHandler itemHandler(){
        return new ItemStackHandler(9) {
            @Nonnull
            @Override
            public ItemStack getStackInSlot(int slot){
                return AdvancedCollectorContainer.this.validateObjectOrClose() ?
                    AdvancedCollectorContainer.this.object.filter.get(slot) :
                    ItemStack.EMPTY;
            }
        };
    }

    public BlockPos getCollectorPosition(){
        return this.blockEntityPos;
    }
}
