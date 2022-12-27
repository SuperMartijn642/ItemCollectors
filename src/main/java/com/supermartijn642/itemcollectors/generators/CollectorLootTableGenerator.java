package com.supermartijn642.itemcollectors.generators;

import com.supermartijn642.core.generator.LootTableGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.itemcollectors.ItemCollectors;

/**
 * Created 27/12/2022 by SuperMartijn642
 */
public class CollectorLootTableGenerator extends LootTableGenerator {

    public CollectorLootTableGenerator(ResourceCache cache){
        super("itemcollectors", cache);
    }

    @Override
    public void generate(){
        this.dropSelf(ItemCollectors.basic_collector);
        this.dropSelf(ItemCollectors.advanced_collector);
    }
}
