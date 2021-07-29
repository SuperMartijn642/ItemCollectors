package com.supermartijn642.itemcollectors.screen;

import com.supermartijn642.core.gui.TileEntityBaseContainer;
import com.supermartijn642.itemcollectors.CollectorTile;
import com.supermartijn642.itemcollectors.ItemCollectors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class AdvancedCollectorContainer extends TileEntityBaseContainer<CollectorTile> {

    public AdvancedCollectorContainer(int id, PlayerEntity player, BlockPos pos){
        super(ItemCollectors.advanced_collector_container, id, player, pos);
        this.addSlots();
    }

    @Override
    protected void addSlots(PlayerEntity player, CollectorTile tile){
        for(int i = 0; i < 9; i++)
            this.addSlot(new SlotItemHandler(this.itemHandler(), i, 8 + i * 18, 90) {
                @Override
                public boolean mayPickup(PlayerEntity playerIn){
                    return false;
                }
            });
        this.addPlayerSlots(32, 124);
    }

    @Override
    public ItemStack clicked(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player){
        if(slotId >= 0 && slotId < 9){
            CollectorTile tile = this.getObjectOrClose();
            if(tile != null){
                if(player.inventory.getCarried().isEmpty())
                    tile.filter.set(slotId, ItemStack.EMPTY);
                else{
                    ItemStack stack = player.inventory.getCarried().copy();
                    stack.setCount(1);
                    tile.filter.set(slotId, stack);
                }
            }
            return ItemStack.EMPTY;
        }
        return super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index){
        if(index >= 0 && index < 9){
            CollectorTile tile = this.getObjectOrClose();
            if(tile != null){
                if(player.inventory.getCarried().isEmpty())
                    tile.filter.set(index, ItemStack.EMPTY);
                else{
                    ItemStack stack = player.inventory.getCarried().copy();
                    stack.setCount(1);
                    tile.filter.set(index, stack);
                }
            }
        }else if(!this.getSlot(index).getItem().isEmpty()){
            boolean contains = false;
            int firstEmpty = -1;
            for(int i = 0; i < 9; i++){
                ItemStack stack = this.itemHandler().getStackInSlot(i);
                if(ItemStack.isSame(stack, this.getSlot(index).getItem()) && ItemStack.tagMatches(stack, this.getSlot(index).getItem())){
                    contains = true;
                    break;
                }
                if(stack.isEmpty() && firstEmpty == -1)
                    firstEmpty = i;
            }
            if(!contains && firstEmpty != -1){
                CollectorTile tile = this.getObjectOrClose();
                if(tile != null){
                    ItemStack stack = this.getSlot(index).getItem().copy();
                    stack.setCount(1);
                    tile.filter.set(firstEmpty, stack);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private ItemStackHandler itemHandler(){
        return new ItemStackHandler(9) {
            @Nonnull
            @Override
            public ItemStack getStackInSlot(int slot){
                CollectorTile tile = AdvancedCollectorContainer.this.getObjectOrClose();
                return tile == null ? ItemStack.EMPTY : tile.filter.get(slot);
            }
        };
    }

    public BlockPos getTilePos(){
        return this.tilePos;
    }
}
