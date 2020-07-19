package com.supermartijn642.itemcollectors.packet;

import com.supermartijn642.itemcollectors.CollectorTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class PacketDecreaseYRange extends CollectorPacket<PacketDecreaseYRange> {
    public PacketDecreaseYRange(BlockPos pos){
        super(pos);
    }

    public PacketDecreaseYRange(){
    }

    @Override
    protected void handle(PacketDecreaseYRange message, EntityPlayer player, World world, CollectorTile tile){
        tile.setRangeY(tile.rangeY - 1);
    }
}
