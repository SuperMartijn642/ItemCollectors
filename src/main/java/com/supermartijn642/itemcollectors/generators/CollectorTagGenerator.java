package com.supermartijn642.itemcollectors.generators;

import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.core.generator.TagGenerator;
import com.supermartijn642.itemcollectors.ItemCollectors;

/**
 * Created 27/12/2022 by SuperMartijn642
 */
public class CollectorTagGenerator extends TagGenerator {

    public CollectorTagGenerator(ResourceCache cache){
        super("itemcollectors", cache);
    }

    @Override
    public void generate(){
        this.blockMineableWithPickaxe().add(ItemCollectors.basic_collector).add(ItemCollectors.advanced_collector);
    }
}
