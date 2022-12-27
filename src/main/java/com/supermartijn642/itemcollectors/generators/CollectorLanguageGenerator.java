package com.supermartijn642.itemcollectors.generators;

import com.supermartijn642.core.generator.LanguageGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.itemcollectors.ItemCollectors;

/**
 * Created 27/12/2022 by SuperMartijn642
 */
public class CollectorLanguageGenerator extends LanguageGenerator {

    public CollectorLanguageGenerator(ResourceCache cache){
        super("itemcollectors", cache, "en_us");
    }

    @Override
    public void generate(){
        // Collectors
        this.block(ItemCollectors.basic_collector, "Basic Item Collector");
        this.translation("itemcollectors.basic_collector.info", "Can pick up items in an area around itself");
        this.translation("itemcollectors.basic_collector.info.range", "Range: %s blocks");
        this.block(ItemCollectors.advanced_collector, "Advanced Item Collector");
        this.translation("itemcollectors.advanced_collector.info", "Can pick up items in an area around itself, items can be filtered");

        // Screens
        this.translation("gui.itemcollectors.basic_collector.range", "Range (%1$dx%2$dx%3$d)");
        this.translation("gui.itemcollectors.basic_collector.range.increase", "Increase Range");
        this.translation("gui.itemcollectors.basic_collector.range.decrease", "Decrease Range");
        this.translation("gui.itemcollectors.advanced_collector.whitelist.on", "Whitelist");
        this.translation("gui.itemcollectors.advanced_collector.whitelist.off", "Blacklist");
        this.translation("gui.itemcollectors.advanced_collector.durability.on", "Match NBT");
        this.translation("gui.itemcollectors.advanced_collector.durability.off", "Ignore NBT");
        this.translation("gui.itemcollectors.advanced_collector.filter", "Filter");
        this.translation("gui.itemcollectors.advanced_collector.show_area.on", "Range Shown");
        this.translation("gui.itemcollectors.advanced_collector.show_area.off", "Range Hidden");
    }
}
