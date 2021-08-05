package com.supermartijn642.itemcollectors.packet;

import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.core.network.TileEntityBasePacket;
import com.supermartijn642.itemcollectors.CollectorTile;
import net.minecraft.core.BlockPos;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class PacketIncreaseZRange extends TileEntityBasePacket<CollectorTile> {

    public PacketIncreaseZRange(){
    }

    public PacketIncreaseZRange(BlockPos pos){
        super(pos);
    }

    @Override
    protected void handle(CollectorTile tile, PacketContext context){
        tile.setRangeZ(tile.rangeZ + 1);
    }
}
