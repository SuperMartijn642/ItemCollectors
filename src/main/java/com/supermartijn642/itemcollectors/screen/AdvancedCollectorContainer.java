package com.supermartijn642.itemcollectors.screen;

import com.supermartijn642.core.gui.BaseContainerType;
import com.supermartijn642.core.gui.BlockEntityBaseContainer;
import com.supermartijn642.core.gui.CustomSlot;
import com.supermartijn642.itemcollectors.CollectorBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class AdvancedCollectorContainer extends BlockEntityBaseContainer<CollectorBlockEntity> {

    public AdvancedCollectorContainer(BaseContainerType<?> containerType, PlayerEntity player, World level, BlockPos pos){
        super(containerType, player, level, pos);
        this.addSlots();
    }

    @Override
    protected void addSlots(PlayerEntity player, CollectorBlockEntity entity){
        for(int i = 0; i < 9; i++){
            int index = i;
            this.addSlot(CustomSlot.builder()
                .position(8 + i * 18, 90)
                .canInsertExtract(false)
                .getter(() -> this.object.getFilterStack(index))
                .build().getVanillaSlot()
            );
        }
        this.addPlayerSlots(32, 124);
    }

    @Override
    public ItemStack clicked(int slot, int dragType, ClickType clickType, PlayerEntity player){
        if(slot >= 0 && slot < 9){
            if(this.player.inventory.getCarried().isEmpty())
                this.object.setFilterStack(slot, ItemStack.EMPTY);
            else{
                ItemStack stack = this.player.inventory.getCarried().copy();
                stack.setCount(1);
                this.object.setFilterStack(slot, stack);
            }
            return ItemStack.EMPTY;
        }
        return super.clicked(slot, dragType, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index){
        if(index >= 0 && index < 9){
            if(this.player.inventory.getCarried().isEmpty())
                this.object.setFilterStack(index, ItemStack.EMPTY);
            else{
                ItemStack stack = this.player.inventory.getCarried().copy();
                stack.setCount(1);
                this.object.setFilterStack(index, stack);
            }
        }else if(!this.getSlot(index).getItem().isEmpty()){
            boolean contains = false;
            int firstEmpty = -1;
            for(int i = 0; i < 9; i++){
                ItemStack stack = this.object.getFilterStack(i);
                if(ItemStack.isSame(stack, this.getSlot(index).getItem()) && ItemStack.tagMatches(stack, this.getSlot(index).getItem())){
                    contains = true;
                    break;
                }
                if(stack.isEmpty() && firstEmpty == -1)
                    firstEmpty = i;
            }
            if(!contains && firstEmpty != -1){
                ItemStack stack = this.getSlot(index).getItem().copy();
                stack.setCount(1);
                this.object.setFilterStack(firstEmpty, stack);
            }
        }
        return ItemStack.EMPTY;
    }

    public BlockPos getCollectorPosition(){
        return this.blockEntityPos;
    }
}
