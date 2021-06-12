package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.render.RenderUtils;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;

/**
 * Created 6/9/2021 by SuperMartijn642
 */
public class CollectorTileRenderer extends TileEntityRenderer<CollectorTile> {

    @Override
    public void render(CollectorTile tile, double x, double y, double z, float partialTicks, int destroyStage){
        if(tile.showArea)
            RenderUtils.renderBox(tile.getAffectedArea(), 245 / 255f, 212 / 255f, 66 / 255f);
    }
}
