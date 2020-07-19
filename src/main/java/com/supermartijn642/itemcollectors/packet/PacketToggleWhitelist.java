package com.supermartijn642.itemcollectors.packet;

import com.supermartijn642.itemcollectors.CollectorTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class PacketToggleWhitelist extends CollectorPacket<PacketToggleWhitelist> {
    public PacketToggleWhitelist(BlockPos pos){
        super(pos);
    }

    public PacketToggleWhitelist(){
    }

    @Override
    protected void handle(PacketToggleWhitelist message, EntityPlayer player, World world, CollectorTile tile){
        tile.filterWhitelist = !tile.filterWhitelist;
        tile.dataChanged();
    }
}
