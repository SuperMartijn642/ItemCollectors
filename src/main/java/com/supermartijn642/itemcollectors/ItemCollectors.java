package com.supermartijn642.itemcollectors;

import com.supermartijn642.itemcollectors.packet.*;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorContainer;
import com.supermartijn642.itemcollectors.screen.BasicCollectorContainer;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
@Mod("itemcollectors")
public class ItemCollectors {

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation("itemcollectors", "main"), () -> "1", "1"::equals, "1"::equals);

    @ObjectHolder("itemcollectors:basic_collector")
    public static Block basic_collector;
    @ObjectHolder("itemcollectors:advanced_collector")
    public static Block advanced_collector;

    @ObjectHolder("itemcollectors:basic_collector_tile")
    public static TileEntityType<CollectorTile> basic_collector_tile;
    @ObjectHolder("itemcollectors:advanced_collector_tile")
    public static TileEntityType<CollectorTile> advanced_collector_tile;

    @ObjectHolder("itemcollectors:basic_collector_container")
    public static ContainerType<BasicCollectorContainer> basic_collector_container;
    @ObjectHolder("itemcollectors:advanced_collector_container")
    public static ContainerType<AdvancedCollectorContainer> advanced_collector_container;

    public ItemCollectors(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);

        CHANNEL.registerMessage(0, PacketIncreaseXRange.class, PacketIncreaseXRange::encode, PacketIncreaseXRange::decode, PacketIncreaseXRange::handle);
        CHANNEL.registerMessage(1, PacketDecreaseXRange.class, PacketDecreaseXRange::encode, PacketDecreaseXRange::decode, PacketDecreaseXRange::handle);
        CHANNEL.registerMessage(2, PacketIncreaseYRange.class, PacketIncreaseYRange::encode, PacketIncreaseYRange::decode, PacketIncreaseYRange::handle);
        CHANNEL.registerMessage(3, PacketDecreaseYRange.class, PacketDecreaseYRange::encode, PacketDecreaseYRange::decode, PacketDecreaseYRange::handle);
        CHANNEL.registerMessage(4, PacketIncreaseZRange.class, PacketIncreaseZRange::encode, PacketIncreaseZRange::decode, PacketIncreaseZRange::handle);
        CHANNEL.registerMessage(5, PacketDecreaseZRange.class, PacketDecreaseZRange::encode, PacketDecreaseZRange::decode, PacketDecreaseZRange::handle);
        CHANNEL.registerMessage(6, PacketToggleWhitelist.class, PacketToggleWhitelist::encode, PacketToggleWhitelist::decode, PacketToggleWhitelist::handle);
        CHANNEL.registerMessage(7, PacketToggleDurability.class, PacketToggleDurability::encode, PacketToggleDurability::decode, PacketToggleDurability::handle);
    }

    public void init(FMLCommonSetupEvent e){
        DistExecutor.runWhenOn(Dist.CLIENT, () -> ClientProxy::registerScreen);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlockRegistry(final RegistryEvent.Register<Block> e){
            e.getRegistry().register(new CollectorBlock("basic_collector", CollectorTile::basicTile, CollectorTile.BASIC_MAX_RANGE, BasicCollectorContainer::new));
            e.getRegistry().register(new CollectorBlock("advanced_collector", CollectorTile::advancedTile, CollectorTile.ADVANCED_MAX_RANGE, AdvancedCollectorContainer::new));
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
            e.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new BasicCollectorContainer(windowId, inv.player, data.readBlockPos())).setRegistryName("basic_collector_container"));
            e.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new AdvancedCollectorContainer(windowId, inv.player, data.readBlockPos())).setRegistryName("advanced_collector_container"));
        }
    }

}
