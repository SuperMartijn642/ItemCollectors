package com.supermartijn642.itemcollectors;

import com.supermartijn642.itemcollectors.packet.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
@Mod(modid = ItemCollectors.MODID, name = ItemCollectors.NAME, version = ItemCollectors.VERSION)
public class ItemCollectors {

    public static final String MODID = "itemcollectors";
    public static final String NAME = "Item Collectors";
    public static final String VERSION = "1.0.5";

    @Mod.Instance
    public static ItemCollectors instance;

    public static SimpleNetworkWrapper channel;

    @GameRegistry.ObjectHolder("itemcollectors:basic_collector")
    public static Block basic_collector;
    @GameRegistry.ObjectHolder("itemcollectors:advanced_collector")
    public static Block advanced_collector;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e){
        channel = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

        channel.registerMessage(PacketIncreaseXRange.class, PacketIncreaseXRange.class, 0, Side.SERVER);
        channel.registerMessage(PacketDecreaseXRange.class, PacketDecreaseXRange.class, 1, Side.SERVER);
        channel.registerMessage(PacketIncreaseYRange.class, PacketIncreaseYRange.class, 2, Side.SERVER);
        channel.registerMessage(PacketDecreaseYRange.class, PacketDecreaseYRange.class, 3, Side.SERVER);
        channel.registerMessage(PacketIncreaseZRange.class, PacketIncreaseZRange.class, 4, Side.SERVER);
        channel.registerMessage(PacketDecreaseZRange.class, PacketDecreaseZRange.class, 5, Side.SERVER);

        channel.registerMessage(PacketToggleWhitelist.class, PacketToggleWhitelist.class, 6, Side.SERVER);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e){
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

    @Mod.EventBusSubscriber
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlockRegistry(final RegistryEvent.Register<Block> e){
            e.getRegistry().register(new CollectorBlock("basic_collector", CollectorTile::basicTile, 0));
            e.getRegistry().register(new CollectorBlock("advanced_collector", CollectorTile::advancedTile, 1));
            GameRegistry.registerTileEntity(CollectorTile.BasicCollectorTile.class, new ResourceLocation(MODID, "basic_collector_tile"));
            GameRegistry.registerTileEntity(CollectorTile.AdvancedCollectorTile.class, new ResourceLocation(MODID, "advanced_collector_tile"));
        }

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> e){
            e.getRegistry().register(new ItemBlock(basic_collector).setRegistryName("basic_collector"));
            e.getRegistry().register(new ItemBlock(advanced_collector).setRegistryName("advanced_collector"));
        }
    }

}
