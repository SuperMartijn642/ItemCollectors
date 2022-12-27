package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.block.BaseBlock;
import com.supermartijn642.core.block.BaseBlockEntityType;
import com.supermartijn642.core.gui.BaseContainerType;
import com.supermartijn642.core.item.BaseBlockItem;
import com.supermartijn642.core.item.CreativeItemGroup;
import com.supermartijn642.core.item.ItemProperties;
import com.supermartijn642.core.network.PacketChannel;
import com.supermartijn642.core.registry.GeneratorRegistrationHandler;
import com.supermartijn642.core.registry.RegistrationHandler;
import com.supermartijn642.core.registry.RegistryEntryAcceptor;
import com.supermartijn642.itemcollectors.generators.*;
import com.supermartijn642.itemcollectors.packet.*;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
@Mod("itemcollectors")
public class ItemCollectors {

    public static final PacketChannel CHANNEL = PacketChannel.create("itemcollectors");

    @RegistryEntryAcceptor(namespace = "itemcollectors", identifier = "basic_collector", registry = RegistryEntryAcceptor.Registry.BLOCKS)
    public static BaseBlock basic_collector;
    @RegistryEntryAcceptor(namespace = "itemcollectors", identifier = "advanced_collector", registry = RegistryEntryAcceptor.Registry.BLOCKS)
    public static BaseBlock advanced_collector;

    @RegistryEntryAcceptor(namespace = "itemcollectors", identifier = "basic_collector_tile", registry = RegistryEntryAcceptor.Registry.BLOCK_ENTITY_TYPES)
    public static BaseBlockEntityType<CollectorBlockEntity> basic_collector_tile;
    @RegistryEntryAcceptor(namespace = "itemcollectors", identifier = "advanced_collector_tile", registry = RegistryEntryAcceptor.Registry.BLOCK_ENTITY_TYPES)
    public static BaseBlockEntityType<CollectorBlockEntity> advanced_collector_tile;

    @RegistryEntryAcceptor(namespace = "itemcollectors", identifier = "filter_collector_container", registry = RegistryEntryAcceptor.Registry.MENU_TYPES)
    public static BaseContainerType<AdvancedCollectorContainer> filter_collector_container;

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

        register();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ItemCollectorsClient::register);
        registerGenerators();
    }

    private static void register(){
        RegistrationHandler handler = RegistrationHandler.get("itemcollectors");
        // Blocks
        handler.registerBlock("basic_collector", () -> new CollectorBlock(() -> basic_collector_tile, ItemCollectorsConfig.basicCollectorMaxRange, ItemCollectorsConfig.basicCollectorFilter));
        handler.registerBlock("advanced_collector", () -> new CollectorBlock(() -> advanced_collector_tile, ItemCollectorsConfig.advancedCollectorMaxRange, ItemCollectorsConfig.advancedCollectorFilter));
        // Block entity types
        handler.registerBlockEntityType("basic_collector_tile", () -> BaseBlockEntityType.create(CollectorBlockEntity::basicCollectorEntity, basic_collector));
        handler.registerBlockEntityType("advanced_collector_tile", () -> BaseBlockEntityType.create(CollectorBlockEntity::advancedCollectorEntity, advanced_collector));
        // Items
        handler.registerItem("basic_collector", () -> new BaseBlockItem(basic_collector, ItemProperties.create().group(CreativeItemGroup.getDecoration())));
        handler.registerItem("advanced_collector", () -> new BaseBlockItem(advanced_collector, ItemProperties.create().group(CreativeItemGroup.getDecoration())));
        // Container type
        handler.registerMenuType("filter_collector_container", () -> BaseContainerType.create(
            (container, data) -> data.writeBlockPos(container.getCollectorPosition()),
            (player, data) -> new AdvancedCollectorContainer(filter_collector_container, player, ClientUtils.getWorld(), data.readBlockPos())
        ));
    }

    private static void registerGenerators(){
        GeneratorRegistrationHandler handler = GeneratorRegistrationHandler.get("itemcollectors");
        handler.addGenerator(CollectorModelGenerator::new);
        handler.addGenerator(CollectorBlockStateGenerator::new);
        handler.addGenerator(CollectorLanguageGenerator::new);
        handler.addGenerator(CollectorLootTableGenerator::new);
        handler.addGenerator(CollectorRecipeGenerator::new);
        handler.addGenerator(CollectorTagGenerator::new);
    }
}
