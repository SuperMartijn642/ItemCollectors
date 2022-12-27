package com.supermartijn642.itemcollectors.generators;

import com.supermartijn642.core.generator.RecipeGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.itemcollectors.ItemCollectors;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

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
        this.shaped(ItemCollectors.basic_collector)
            .pattern(" A ")
            .pattern(" B ")
            .pattern("BBB")
            .input('A', Tags.Items.ENDER_PEARLS)
            .input('B', Tags.Items.OBSIDIAN)
            .unlockedBy(Tags.Items.ENDER_PEARLS);
        // Advanced item collector
        this.shaped(ItemCollectors.advanced_collector)
            .pattern(" A ")
            .pattern(" B ")
            .pattern("CCC")
            .input('A', Items.ENDER_EYE)
            .input('B', ItemCollectors.basic_collector)
            .input('C', Tags.Items.OBSIDIAN)
            .unlockedBy(ItemCollectors.basic_collector);
    }
}
