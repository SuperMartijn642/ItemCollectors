package com.supermartijn642.itemcollectors.packet;

import com.supermartijn642.core.network.BlockEntityBasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.itemcollectors.CollectorBlockEntity;
import net.minecraft.core.BlockPos;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class PacketIncreaseYRange extends BlockEntityBasePacket<CollectorBlockEntity> {

    public PacketIncreaseYRange(){
    }

    public PacketIncreaseYRange(BlockPos pos){
        super(pos);
    }

    @Override
    protected void handle(CollectorBlockEntity entity, PacketContext context){
        entity.setRangeY(entity.rangeY + 1);
    }
}
