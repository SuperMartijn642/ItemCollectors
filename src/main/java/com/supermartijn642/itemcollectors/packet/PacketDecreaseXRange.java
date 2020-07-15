package com.supermartijn642.itemcollectors.packet;

import com.supermartijn642.itemcollectors.CollectorTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class PacketDecreaseXRange extends CollectorPacket {
    public PacketDecreaseXRange(BlockPos pos){
        super(pos);
    }

    public PacketDecreaseXRange(PacketBuffer buffer){
        super(buffer);
    }

    public static PacketDecreaseXRange decode(PacketBuffer buffer){
        return new PacketDecreaseXRange(buffer);
    }

    @Override
    protected void handle(PlayerEntity player, World world, CollectorTile tile){
        tile.setRangeX(tile.rangeX - 1);
    }
}
