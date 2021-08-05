package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.network.PacketChannel;
import com.supermartijn642.itemcollectors.packet.*;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
@Mod("itemcollectors")
public class ItemCollectors {

    public static final PacketChannel CHANNEL = PacketChannel.create("itemcollectors");

    @ObjectHolder("itemcollectors:basic_collector")
    public static Block basic_collector;
    @ObjectHolder("itemcollectors:advanced_collector")
    public static Block advanced_collector;

    @ObjectHolder("itemcollectors:basic_collector_tile")
    public static BlockEntityType<CollectorTile> basic_collector_tile;
    @ObjectHolder("itemcollectors:advanced_collector_tile")
    public static BlockEntityType<CollectorTile> advanced_collector_tile;

    @ObjectHolder("itemcollectors:filter_collector_container")
    public static MenuType<AdvancedCollectorContainer> advanced_collector_container;

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

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onBlockRegistry(final RegistryEvent.Register<Block> e){
            e.getRegistry().register(new CollectorBlock("basic_collector", CollectorTile::basicTile, ItemCollectorsConfig.basicCollectorMaxRange, ItemCollectorsConfig.basicCollectorFilter));
            e.getRegistry().register(new CollectorBlock("advanced_collector", CollectorTile::advancedTile, ItemCollectorsConfig.advancedCollectorMaxRange, ItemCollectorsConfig.advancedCollectorFilter));
        }

        @SubscribeEvent
        public static void onTileRegistry(final RegistryEvent.Register<BlockEntityType<?>> e){
            e.getRegistry().register(BlockEntityType.Builder.of(CollectorTile::basicTile, basic_collector).build(null).setRegistryName("basic_collector_tile"));
            e.getRegistry().register(BlockEntityType.Builder.of(CollectorTile::advancedTile, advanced_collector).build(null).setRegistryName("advanced_collector_tile"));
        }

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> e){
            e.getRegistry().register(new BlockItem(basic_collector, new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)).setRegistryName("basic_collector"));
            e.getRegistry().register(new BlockItem(advanced_collector, new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)).setRegistryName("advanced_collector"));
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<MenuType<?>> e){
            e.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new AdvancedCollectorContainer(windowId, inv.player, data.readBlockPos())).setRegistryName("filter_collector_container"));
        }
    }

}
