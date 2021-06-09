package com.supermartijn642.itemcollectors.packet;

import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.core.network.TileEntityBasePacket;
import com.supermartijn642.itemcollectors.CollectorTile;
import net.minecraft.util.math.BlockPos;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class PacketToggleDurability extends TileEntityBasePacket<CollectorTile> {

    public PacketToggleDurability(){
    }

    public PacketToggleDurability(BlockPos pos){
        super(pos);
    }

    @Override
    protected void handle(CollectorTile tile, PacketContext context){
        tile.filterDurability = !tile.filterDurability;
        tile.dataChanged();
    }
}
