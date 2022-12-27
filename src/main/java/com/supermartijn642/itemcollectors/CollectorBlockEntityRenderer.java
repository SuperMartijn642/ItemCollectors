package com.supermartijn642.itemcollectors;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.render.CustomBlockEntityRenderer;
import com.supermartijn642.core.render.RenderUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.AABB;

import java.util.Random;

/**
 * Created 6/9/2021 by SuperMartijn642
 */
public class CollectorBlockEntityRenderer implements CustomBlockEntityRenderer<CollectorBlockEntity> {

    @Override
    public void render(CollectorBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay){
        if(entity.showArea){
            poseStack.pushPose();
            poseStack.translate(-entity.getBlockPos().getX(), -entity.getBlockPos().getY(), -entity.getBlockPos().getZ());

            AABB area = entity.getAffectedArea().inflate(0.05f);

            Random random = new Random(entity.getBlockPos().hashCode());
            float red = random.nextFloat();
            float green = random.nextFloat();
            float blue = random.nextFloat();
            float alpha = 0.3f;

            RenderUtils.renderBox(poseStack, area, red, green, blue, true);
            RenderUtils.renderBoxSides(poseStack, area, red, green, blue, alpha, true);

            poseStack.popPose();
        }
    }
}
