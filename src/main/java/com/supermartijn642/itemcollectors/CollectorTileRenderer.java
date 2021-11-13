package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

/**
 * Created 6/9/2021 by SuperMartijn642
 */
public class CollectorTileRenderer extends TileEntitySpecialRenderer<CollectorTile> {

    @Override
    public void render(CollectorTile tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
        if(tile.showArea){
            GlStateManager.pushMatrix();
            Vec3d camera = RenderUtils.getCameraPosition();
            GlStateManager.translate(-camera.x, -camera.y, -camera.z);

            AxisAlignedBB area = tile.getAffectedArea().grow(0.05f);

            Random random = new Random(tile.getPos().hashCode());
            float red = random.nextFloat();
            float green = random.nextFloat();
            float blue = random.nextFloat();
            alpha *= 0.3f;

            RenderUtils.renderBox(area, red, green, blue);
            RenderUtils.renderBoxSides(area, red, green, blue, alpha);

            GlStateManager.popMatrix();
        }
    }
}
