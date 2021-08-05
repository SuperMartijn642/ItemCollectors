package com.supermartijn642.itemcollectors.packet;

import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.core.network.TileEntityBasePacket;
import com.supermartijn642.itemcollectors.CollectorTile;
import net.minecraft.core.BlockPos;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class PacketIncreaseXRange extends TileEntityBasePacket<CollectorTile> {

    public PacketIncreaseXRange(){
    }

    public PacketIncreaseXRange(BlockPos pos){
        super(pos);
    }

    @Override
    protected void handle(CollectorTile tile, PacketContext context){
        tile.setRangeX(tile.rangeX + 1);
    }
}
