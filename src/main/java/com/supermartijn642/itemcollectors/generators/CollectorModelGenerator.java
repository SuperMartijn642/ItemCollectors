package com.supermartijn642.itemcollectors.generators;

import com.supermartijn642.core.generator.ModelGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import net.minecraft.resources.ResourceLocation;

/**
 * Created 27/12/2022 by SuperMartijn642
 */
public class CollectorModelGenerator extends ModelGenerator {

    public CollectorModelGenerator(ResourceCache cache){
        super("itemcollectors", cache);
    }

    @Override
    public void generate(){
        ResourceLocation template = new ResourceLocation("itemcollectors", "collector");
        // Block models
        this.model("block/basic_collector").parent(template).texture("all", "basic_collector");
        this.model("block/advanced_collector").parent(template).texture("all", "advanced_collector");
        // Item models
        this.model("item/basic_collector").parent("block/basic_collector");
        this.model("item/advanced_collector").parent("block/basic_collector");
    }
}
