package com.supermartijn642.itemcollectors.packet;

import com.supermartijn642.core.network.BlockEntityBasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.itemcollectors.CollectorBlockEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Created 6/9/2021 by SuperMartijn642
 */
public class PacketToggleShowArea extends BlockEntityBasePacket<CollectorBlockEntity> {

    public PacketToggleShowArea(){}

    public PacketToggleShowArea(BlockPos pos){
        super(pos);
    }

    @Override
    protected void handle(CollectorBlockEntity entity, PacketContext context){
        entity.setShowArea(!entity.showArea);
    }
}
