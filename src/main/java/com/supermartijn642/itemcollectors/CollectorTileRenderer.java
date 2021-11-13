package com.supermartijn642.itemcollectors;

import com.mojang.blaze3d.platform.GlStateManager;
import com.supermartijn642.core.render.RenderUtils;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

/**
 * Created 6/9/2021 by SuperMartijn642
 */
public class CollectorTileRenderer extends TileEntityRenderer<CollectorTile> {

    @Override
    public void render(CollectorTile tile, double x, double y, double z, float partialTicks, int destroyStage){
        if(tile.showArea){
            GlStateManager.pushMatrix();
            Vec3d camera = RenderUtils.getCameraPosition();
            GlStateManager.translated(-camera.x, -camera.y, -camera.z);

            AxisAlignedBB area = tile.getAffectedArea().grow(0.05f);

            Random random = new Random(tile.getPos().hashCode());
            float red = random.nextFloat();
            float green = random.nextFloat();
            float blue = random.nextFloat();
            float alpha = 0.3f;

            RenderUtils.renderBox(area, red, green, blue);
            RenderUtils.renderBoxSides(area, red, green, blue, alpha);

            GlStateManager.popMatrix();
        }
    }
}
