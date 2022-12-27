package com.supermartijn642.itemcollectors.packet;

import com.supermartijn642.core.network.BlockEntityBasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.itemcollectors.CollectorBlockEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class PacketIncreaseZRange extends BlockEntityBasePacket<CollectorBlockEntity> {

    public PacketIncreaseZRange(){
    }

    public PacketIncreaseZRange(BlockPos pos){
        super(pos);
    }

    @Override
    protected void handle(CollectorBlockEntity entity, PacketContext context){
        entity.setRangeZ(entity.rangeZ + 1);
    }
}
