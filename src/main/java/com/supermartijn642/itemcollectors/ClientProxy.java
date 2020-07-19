package com.supermartijn642.itemcollectors;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e){
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ItemCollectors.basic_collector), 0, new ModelResourceLocation(ItemCollectors.basic_collector.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ItemCollectors.advanced_collector), 0, new ModelResourceLocation(ItemCollectors.advanced_collector.getRegistryName(), "inventory"));
    }

}
