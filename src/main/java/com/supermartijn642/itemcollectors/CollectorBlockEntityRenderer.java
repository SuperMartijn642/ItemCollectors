package com.supermartijn642.itemcollectors;

import com.mojang.blaze3d.platform.GlStateManager;
import com.supermartijn642.core.render.CustomBlockEntityRenderer;
import com.supermartijn642.core.render.RenderUtils;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Random;

/**
 * Created 6/9/2021 by SuperMartijn642
 */
public class CollectorBlockEntityRenderer implements CustomBlockEntityRenderer<CollectorBlockEntity> {

    @Override
    public void render(CollectorBlockEntity entity, float partialTicks, int combinedOverlay){
        if(entity.showArea){
            GlStateManager.pushMatrix();
            GlStateManager.translated(-entity.getBlockPos().getX(), -entity.getBlockPos().getY(), -entity.getBlockPos().getZ());

            AxisAlignedBB area = entity.getAffectedArea().inflate(0.05f);

            Random random = new Random(entity.getBlockPos().hashCode());
            float red = random.nextFloat();
            float green = random.nextFloat();
            float blue = random.nextFloat();
            float alpha = 0.3f;

            RenderUtils.renderBox(area, red, green, blue, true);
            RenderUtils.renderBoxSides(area, red, green, blue, alpha, true);

            GlStateManager.popMatrix();
        }
    }
}
