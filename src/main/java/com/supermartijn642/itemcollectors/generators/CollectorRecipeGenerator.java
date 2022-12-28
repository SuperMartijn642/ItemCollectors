package com.supermartijn642.itemcollectors.generators;

import com.supermartijn642.core.generator.RecipeGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.itemcollectors.ItemCollectors;
import net.minecraft.init.Items;

/**
 * Created 27/12/2022 by SuperMartijn642
 */
public class CollectorRecipeGenerator extends RecipeGenerator {

    public CollectorRecipeGenerator(ResourceCache cache){
        super("itemcollectors", cache);
    }

    @Override
    public void generate(){
        // Basic item collector
        this.shaped(ItemCollectors.basic_collector.asItem())
            .pattern(" A ")
            .pattern(" B ")
            .pattern("BBB")
            .input('A', "enderpearl")
            .input('B', "obsidian")
            .unlockedByOreDict("enderpearl");
        // Advanced item collector
        this.shaped(ItemCollectors.advanced_collector.asItem())
            .pattern(" A ")
            .pattern(" B ")
            .pattern("CCC")
            .input('A', Items.ENDER_EYE)
            .input('B', ItemCollectors.basic_collector.asItem())
            .input('C', "obsidian")
            .unlockedBy(ItemCollectors.basic_collector.asItem());
    }
}
