package com.supermartijn642.itemcollectors.packet;

import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.core.network.TileEntityBasePacket;
import com.supermartijn642.itemcollectors.CollectorTile;
import net.minecraft.core.BlockPos;

/**
 * Created 6/9/2021 by SuperMartijn642
 */
public class PacketToggleShowArea extends TileEntityBasePacket<CollectorTile> {

    public PacketToggleShowArea(){}

    public PacketToggleShowArea(BlockPos pos){
        super(pos);
    }

    @Override
    protected void handle(CollectorTile tile, PacketContext context){
        tile.setShowArea(!tile.showArea);
    }
}
