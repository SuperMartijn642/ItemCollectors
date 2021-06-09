package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.network.PacketChannel;
import com.supermartijn642.itemcollectors.packet.*;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorContainer;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
@Mod("itemcollectors")
public class ItemCollectors {

    public static final PacketChannel CHANNEL = PacketChannel.create();

    @ObjectHolder("itemcollectors:basic_collector")
    public static Block basic_collector;
    @ObjectHolder("itemcollectors:advanced_collector")
    public static Block advanced_collector;

    @ObjectHolder("itemcollectors:basic_collector_tile")
    public static TileEntityType<CollectorTile> basic_collector_tile;
    @ObjectHolder("itemcollectors:advanced_collector_tile")
    public static TileEntityType<CollectorTile> advanced_collector_tile;

    @ObjectHolder("itemcollectors:filter_collector_container")
    public static ContainerType<AdvancedCollectorContainer> advanced_collector_container;

    public ItemCollectors(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);

        CHANNEL.registerMessage(PacketIncreaseXRange.class, PacketIncreaseXRange::new, true);
        CHANNEL.registerMessage(PacketDecreaseXRange.class, PacketDecreaseXRange::new, true);
        CHANNEL.registerMessage(PacketIncreaseYRange.class, PacketIncreaseYRange::new, true);
        CHANNEL.registerMessage(PacketDecreaseYRange.class, PacketDecreaseYRange::new, true);
        CHANNEL.registerMessage(PacketIncreaseZRange.class, PacketIncreaseZRange::new, true);
        CHANNEL.registerMessage(PacketDecreaseZRange.class, PacketDecreaseZRange::new, true);
        CHANNEL.registerMessage(PacketToggleWhitelist.class, PacketToggleWhitelist::new, true);
        CHANNEL.registerMessage(PacketToggleDurability.class, PacketToggleDurability::new, true);
    }

    public void init(FMLCommonSetupEvent e){
        DistExecutor.runWhenOn(Dist.CLIENT, () -> ClientProxy::registerScreen);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onBlockRegistry(final RegistryEvent.Register<Block> e){
            e.getRegistry().register(new CollectorBlock("basic_collector", CollectorTile::basicTile, ItemCollectorsConfig.basicCollectorMaxRange, ItemCollectorsConfig.basicCollectorFilter));
            e.getRegistry().register(new CollectorBlock("advanced_collector", CollectorTile::advancedTile, ItemCollectorsConfig.advancedCollectorMaxRange, ItemCollectorsConfig.advancedCollectorFilter));
        }

        @SubscribeEvent
        public static void onTileRegistry(final RegistryEvent.Register<TileEntityType<?>> e){
            e.getRegistry().register(TileEntityType.Builder.create(CollectorTile::basicTile, basic_collector).build(null).setRegistryName("basic_collector_tile"));
            e.getRegistry().register(TileEntityType.Builder.create(CollectorTile::advancedTile, advanced_collector).build(null).setRegistryName("advanced_collector_tile"));
        }

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> e){
            e.getRegistry().register(new BlockItem(basic_collector, new Item.Properties().group(ItemGroup.SEARCH)).setRegistryName("basic_collector"));
            e.getRegistry().register(new BlockItem(advanced_collector, new Item.Properties().group(ItemGroup.SEARCH)).setRegistryName("advanced_collector"));
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> e){
            e.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new AdvancedCollectorContainer(windowId, inv.player, data.readBlockPos())).setRegistryName("filter_collector_container"));
        }
    }

}
