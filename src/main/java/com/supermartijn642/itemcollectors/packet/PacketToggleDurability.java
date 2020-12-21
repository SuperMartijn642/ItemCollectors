package com.supermartijn642.itemcollectors.packet;

import com.supermartijn642.itemcollectors.CollectorTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class PacketToggleDurability extends CollectorPacket {
    public PacketToggleDurability(BlockPos pos){
        super(pos);
    }

    public PacketToggleDurability(PacketBuffer buffer){
        super(buffer);
    }

    public static PacketToggleDurability decode(PacketBuffer buffer){
        return new PacketToggleDurability(buffer);
    }

    @Override
    protected void handle(PlayerEntity player, World world, CollectorTile tile){
        tile.filterDurability = !tile.filterDurability;
        tile.dataChanged();
    }
}
