package com.supermartijn642.itemcollectors;

import com.supermartijn642.itemcollectors.screen.AdvancedCollectorContainer;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

/**
 * Created 7/19/2020 by SuperMartijn642
 */
public class GuiHandler implements IGuiHandler {

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
        BlockPos pos = new BlockPos(x, y, z);
        return new AdvancedCollectorContainer(player, pos);
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
        BlockPos pos = new BlockPos(x, y, z);
        return new AdvancedCollectorScreen(new AdvancedCollectorContainer(player, pos));
    }
}
