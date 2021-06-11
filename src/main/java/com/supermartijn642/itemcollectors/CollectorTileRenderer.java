package com.supermartijn642.itemcollectors;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

/**
 * Created 6/9/2021 by SuperMartijn642
 */
public class CollectorTileRenderer extends TileEntitySpecialRenderer<CollectorTile> {

    @Override
    public void render(CollectorTile tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
        if(!tile.showArea)
            return;

        ClientProxy.drawShape(tile.getAffectedArea(), 245 / 255f, 212 / 255f, 66 / 255f, 1);
    }
}
