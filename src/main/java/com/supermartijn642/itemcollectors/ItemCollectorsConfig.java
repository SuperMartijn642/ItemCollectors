package com.supermartijn642.itemcollectors;

import com.supermartijn642.configlib.api.ConfigBuilders;
import com.supermartijn642.configlib.api.IConfigBuilder;

import java.util.function.Supplier;

/**
 * Created 6/8/2021 by SuperMartijn642
 */
public class ItemCollectorsConfig {

    public static final Supplier<Integer> basicCollectorMaxRange;
    public static final Supplier<Boolean> basicCollectorFilter;
    public static final Supplier<Integer> advancedCollectorMaxRange;
    public static final Supplier<Boolean> advancedCollectorFilter;

    public static final Supplier<Integer> maxInsertions;

    static{
        IConfigBuilder builder = ConfigBuilders.newTomlConfig("itemcollectors", null, false);

        builder.push("General");
        basicCollectorMaxRange = builder.comment("What should be the maximum range of the basic item collector?").define("basicCollectorMaxRange", 5, 1, 10);
        basicCollectorFilter = builder.comment("Should the basic item collector have a filter?").define("basicCollectorFilter", false);
        advancedCollectorMaxRange = builder.comment("What should be the maximum range of the advanced item collector?").define("advancedCollectorMaxRange", 7, 1, 10);
        advancedCollectorFilter = builder.comment("Should the advanced item collector have a filter?").define("advancedCollectorFilter", true);
        builder.pop();

        builder.push("Performance");
        maxInsertions = builder.comment("What should be the maximum number of items an item collector should try to pick up and insert in a tick? Reducing this number may improve performance when there's a lot of items laying around, for example when storage is full. A value of -1 indicates no limit on the number of insertions.").define("maxInsertions", 20, -1, 10000);
        builder.pop();

        builder.build();
    }

}
