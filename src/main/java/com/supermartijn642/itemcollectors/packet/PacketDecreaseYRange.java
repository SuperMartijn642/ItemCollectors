package com.supermartijn642.itemcollectors.packet;

import com.supermartijn642.itemcollectors.CollectorTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class PacketDecreaseYRange extends CollectorPacket {
    public PacketDecreaseYRange(BlockPos pos){
        super(pos);
    }

    public PacketDecreaseYRange(PacketBuffer buffer){
        super(buffer);
    }

    public static PacketDecreaseYRange decode(PacketBuffer buffer){
        return new PacketDecreaseYRange(buffer);
    }

    @Override
    protected void handle(PlayerEntity player, World world, CollectorTile tile){
        tile.setRangeY(tile.rangeY - 1);
    }
}
