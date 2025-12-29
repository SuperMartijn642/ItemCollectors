package com.supermartijn642.itemcollectors.generators;

import com.supermartijn642.core.generator.ModelGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import net.minecraft.resources.Identifier;

/**
 * Created 27/12/2022 by SuperMartijn642
 */
public class CollectorModelGenerator extends ModelGenerator {

    public CollectorModelGenerator(ResourceCache cache){
        super("itemcollectors", cache);
    }

    @Override
    public void generate(){
        Identifier template = Identifier.fromNamespaceAndPath("itemcollectors", "collector");
        this.model("block/basic_collector").parent(template).texture("all", "basic_collector");
        this.model("block/advanced_collector").parent(template).texture("all", "advanced_collector");
    }
}
