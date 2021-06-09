package com.supermartijn642.itemcollectors;

import com.supermartijn642.configlib.ModConfigBuilder;

import java.util.function.Supplier;

/**
 * Created 6/8/2021 by SuperMartijn642
 */
public class ItemCollectorsConfig {

    public static final Supplier<Integer> basicCollectorMaxRange;
    public static final Supplier<Boolean> basicCollectorFilter;
    public static final Supplier<Integer> advancedCollectorMaxRange;
    public static final Supplier<Boolean> advancedCollectorFilter;

    static{
        ModConfigBuilder builder = new ModConfigBuilder();

        builder.push("General");
        basicCollectorMaxRange = builder.comment("What should be the maximum range of the basic item collector?").define("basicCollectorMaxRange", 5, 1, 10);
        basicCollectorFilter = builder.comment("Should the basic item collector have a filter?").define("basicCollectorFilter", false);
        advancedCollectorMaxRange = builder.comment("What should be the maximum range of the advanced item collector?").define("advancedCollectorMaxRange", 7, 1, 10);
        advancedCollectorFilter = builder.comment("Should the advanced item collector have a filter?").define("advancedCollectorFilter", true);
        builder.pop();

        builder.build();
    }

}
