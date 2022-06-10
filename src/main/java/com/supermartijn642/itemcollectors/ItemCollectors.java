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
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Objects;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
@Mod("itemcollectors")
public class ItemCollectors {

    public static final PacketChannel CHANNEL = PacketChannel.create("itemcollectors");

    @ObjectHolder(value = "itemcollectors:basic_collector", registryName = "minecraft:block")
    public static Block basic_collector;
    @ObjectHolder(value = "itemcollectors:advanced_collector", registryName = "minecraft:block")
    public static Block advanced_collector;

    @ObjectHolder(value = "itemcollectors:basic_collector_tile", registryName = "minecraft:block_entity_type")
    public static BlockEntityType<CollectorTile> basic_collector_tile;
    @ObjectHolder(value = "itemcollectors:advanced_collector_tile", registryName = "minecraft:block_entity_type")
    public static BlockEntityType<CollectorTile> advanced_collector_tile;

    @ObjectHolder(value = "itemcollectors:filter_collector_container", registryName = "minecraft:menu")
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
        public static void onRegisterEvent(RegisterEvent e){
            if(e.getRegistryKey().equals(ForgeRegistries.Keys.BLOCKS))
                onBlockRegistry(Objects.requireNonNull(e.getForgeRegistry()));
            else if(e.getRegistryKey().equals(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES))
                onTileRegistry(Objects.requireNonNull(e.getForgeRegistry()));
            else if(e.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS))
                onItemRegistry(Objects.requireNonNull(e.getForgeRegistry()));
            else if(e.getRegistryKey().equals(ForgeRegistries.Keys.CONTAINER_TYPES))
                onContainerRegistry(Objects.requireNonNull(e.getForgeRegistry()));
        }

        public static void onBlockRegistry(IForgeRegistry<Block> registry){
            registry.register("basic_collector", new CollectorBlock("basic_collector", CollectorTile::basicTile, ItemCollectorsConfig.basicCollectorMaxRange, ItemCollectorsConfig.basicCollectorFilter));
            registry.register("advanced_collector", new CollectorBlock("advanced_collector", CollectorTile::advancedTile, ItemCollectorsConfig.advancedCollectorMaxRange, ItemCollectorsConfig.advancedCollectorFilter));
        }

        public static void onTileRegistry(IForgeRegistry<BlockEntityType<?>> registry){
            registry.register("basic_collector_tile", BlockEntityType.Builder.of(CollectorTile::basicTile, basic_collector).build(null));
            registry.register("advanced_collector_tile", BlockEntityType.Builder.of(CollectorTile::advancedTile, advanced_collector).build(null));
        }

        public static void onItemRegistry(IForgeRegistry<Item> registry){
            registry.register("basic_collector", new BlockItem(basic_collector, new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
            registry.register("advanced_collector", new BlockItem(advanced_collector, new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
        }

        public static void onContainerRegistry(IForgeRegistry<MenuType<?>> registry){
            registry.register("filter_collector_container", IForgeMenuType.create((windowId, inv, data) -> new AdvancedCollectorContainer(windowId, inv.player, data.readBlockPos())));
        }
    }

}
