package com.supermartijn642.itemcollectors.screen;

import com.supermartijn642.itemcollectors.CollectorTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class BasicCollectorContainer extends CollectorContainer {

    public BasicCollectorContainer(EntityPlayer player, BlockPos pos){
        super(player, pos, 202, 82, false);
    }

    @Override
    protected void addSlots(CollectorTile tile){
    }
}
