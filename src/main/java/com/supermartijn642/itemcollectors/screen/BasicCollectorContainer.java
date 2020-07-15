package com.supermartijn642.itemcollectors.screen;

import com.supermartijn642.itemcollectors.CollectorTile;
import com.supermartijn642.itemcollectors.ItemCollectors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class BasicCollectorContainer extends CollectorContainer {

    public BasicCollectorContainer(int id, PlayerEntity player, BlockPos pos){
        super(ItemCollectors.basic_collector_container, id, player, pos, 202, 82, false);
    }

    @Override
    protected void addSlots(CollectorTile tile){
    }
}
