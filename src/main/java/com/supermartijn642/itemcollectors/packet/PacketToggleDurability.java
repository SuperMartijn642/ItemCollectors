package com.supermartijn642.itemcollectors.packet;

import com.supermartijn642.itemcollectors.CollectorTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class PacketToggleDurability extends CollectorPacket<PacketToggleDurability> {
    public PacketToggleDurability(BlockPos pos){
        super(pos);
    }

    public PacketToggleDurability(){
    }

    @Override
    protected void handle(PacketToggleDurability message, EntityPlayer player, World world, CollectorTile tile){
        tile.filterDurability = !tile.filterDurability;
        tile.dataChanged();
    }
}
