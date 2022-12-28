package com.supermartijn642.itemcollectors.generators;

import com.supermartijn642.core.generator.BlockStateGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.core.registry.Registries;
import com.supermartijn642.itemcollectors.CollectorBlock;
import com.supermartijn642.itemcollectors.ItemCollectors;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

/**
 * Created 27/12/2022 by SuperMartijn642
 */
public class CollectorBlockStateGenerator extends BlockStateGenerator {

    public CollectorBlockStateGenerator(ResourceCache cache){
        super("itemcollectors", cache);
    }

    @Override
    public void generate(){
        this.createCollectorBlockState(ItemCollectors.basic_collector);
        this.createCollectorBlockState(ItemCollectors.advanced_collector);
    }

    private void createCollectorBlockState(Block block){
        ResourceLocation model = new ResourceLocation("itemcollectors", "block/" + Registries.BLOCKS.getIdentifier(block).getResourcePath());
        this.blockState(block).variantsForProperty(CollectorBlock.DIRECTION,
            (state, variant) -> {
                EnumFacing direction = state.get(CollectorBlock.DIRECTION);
                int xRotation = direction == EnumFacing.DOWN ? 0 : direction == EnumFacing.UP ? 180 : 90;
                int yRotation = direction.getAxis().isVertical() ? 0 : (int)direction.getHorizontalAngle();
                variant.model(model, xRotation, yRotation);
            }
        );
    }
}
