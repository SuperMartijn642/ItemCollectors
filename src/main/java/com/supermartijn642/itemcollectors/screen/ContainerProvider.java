package com.supermartijn642.itemcollectors.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public interface ContainerProvider {

    Container createContainer(int id, PlayerEntity player, BlockPos pos);
}
