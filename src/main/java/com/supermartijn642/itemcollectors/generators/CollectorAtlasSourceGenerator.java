package com.supermartijn642.itemcollectors.generators;

import com.supermartijn642.core.generator.AtlasSourceGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.itemcollectors.screen.*;

/**
 * Created 30/06/2025 by SuperMartijn642
 */
public class CollectorAtlasSourceGenerator extends AtlasSourceGenerator {

    public CollectorAtlasSourceGenerator(ResourceCache cache){
        super("itemcollectors", cache);
    }

    @Override
    public void generate(){
        this.guiAtlas()
            .texture(WhitelistButton.BUTTONS)
            .texture(DurabilityButton.BUTTONS)
            .texture(ShowAreaButton.BUTTONS)
            .texture(ArrowButton.BUTTONS)
            .texture(AdvancedCollectorScreen.BACKGROUND);
    }
}
