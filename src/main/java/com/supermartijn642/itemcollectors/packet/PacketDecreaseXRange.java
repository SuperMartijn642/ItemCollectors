package com.supermartijn642.itemcollectors.packet;

import com.supermartijn642.core.network.BlockEntityBasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.itemcollectors.CollectorBlockEntity;
import net.minecraft.core.BlockPos;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class PacketDecreaseXRange extends BlockEntityBasePacket<CollectorBlockEntity> {

    public PacketDecreaseXRange(){
    }

    public PacketDecreaseXRange(BlockPos pos){
        super(pos);
    }

    @Override
    protected void handle(CollectorBlockEntity entity, PacketContext context){
        entity.setRangeX(entity.rangeX - 1);
    }
}
