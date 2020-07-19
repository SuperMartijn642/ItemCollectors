package com.supermartijn642.itemcollectors.packet;

import com.supermartijn642.itemcollectors.CollectorTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class PacketIncreaseZRange extends CollectorPacket<PacketIncreaseZRange> {
    public PacketIncreaseZRange(BlockPos pos){
        super(pos);
    }

    public PacketIncreaseZRange(){
    }

    @Override
    protected void handle(PacketIncreaseZRange message, EntityPlayer player, World world, CollectorTile tile){
        tile.setRangeZ(tile.rangeZ + 1);
    }
}
