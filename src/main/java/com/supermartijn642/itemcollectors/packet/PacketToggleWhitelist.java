package com.supermartijn642.itemcollectors.packet;

import com.supermartijn642.itemcollectors.CollectorTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class PacketToggleWhitelist extends CollectorPacket {
    public PacketToggleWhitelist(BlockPos pos){
        super(pos);
    }

    public PacketToggleWhitelist(PacketBuffer buffer){
        super(buffer);
    }

    public static PacketToggleWhitelist decode(PacketBuffer buffer){
        return new PacketToggleWhitelist(buffer);
    }

    @Override
    protected void handle(PlayerEntity player, World world, CollectorTile tile){
        tile.filterWhitelist = !tile.filterWhitelist;
        tile.dataChanged();
    }
}
