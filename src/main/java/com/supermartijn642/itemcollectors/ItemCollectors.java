package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.network.PacketChannel;
import com.supermartijn642.itemcollectors.packet.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
@Mod(modid = ItemCollectors.MODID, name = ItemCollectors.NAME, version = ItemCollectors.VERSION, dependencies = ItemCollectors.DEPENDENCIES)
public class ItemCollectors {

    public static final String MODID = "itemcollectors";
    public static final String NAME = "Item Collectors";
    public static final String VERSION = "1.1.2";
    public static final String DEPENDENCIES = "required-after:supermartijn642configlib@[1.0.7,1.1.0);required-after:supermartijn642corelib@[1.0.6,1.1.0)";

    @Mod.Instance
    public static ItemCollectors instance;

    public static final PacketChannel CHANNEL = PacketChannel.create("itemcollectors");

    @GameRegistry.ObjectHolder("itemcollectors:basic_collector")
    public static Block basic_collector;
    @GameRegistry.ObjectHolder("itemcollectors:advanced_collector")
    public static Block advanced_collector;

    public ItemCollectors(){
        CHANNEL.registerMessage(PacketIncreaseXRange.class, PacketIncreaseXRange::new, true);
        CHANNEL.registerMessage(PacketDecreaseXRange.class, PacketDecreaseXRange::new, true);
        CHANNEL.registerMessage(PacketIncreaseYRange.class, PacketIncreaseYRange::new, true);
        CHANNEL.registerMessage(PacketDecreaseYRange.class, PacketDecreaseYRange::new, true);
        CHANNEL.registerMessage(PacketIncreaseZRange.class, PacketIncreaseZRange::new, true);
        CHANNEL.registerMessage(PacketDecreaseZRange.class, PacketDecreaseZRange::new, true);
        CHANNEL.registerMessage(PacketToggleWhitelist.class, PacketToggleWhitelist::new, true);
        CHANNEL.registerMessage(PacketToggleDurability.class, PacketToggleDurability::new, true);
        CHANNEL.registerMessage(PacketToggleShowArea.class, PacketToggleShowArea::new, true);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e){
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

    @Mod.EventBusSubscriber
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onBlockRegistry(final RegistryEvent.Register<Block> e){
            e.getRegistry().register(new CollectorBlock("basic_collector", CollectorTile::basicTile, ItemCollectorsConfig.basicCollectorMaxRange, ItemCollectorsConfig.basicCollectorFilter));
            e.getRegistry().register(new CollectorBlock("advanced_collector", CollectorTile::advancedTile, ItemCollectorsConfig.advancedCollectorMaxRange, ItemCollectorsConfig.advancedCollectorFilter));
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
