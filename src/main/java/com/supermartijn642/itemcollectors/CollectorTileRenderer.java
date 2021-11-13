package com.supermartijn642.itemcollectors;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.render.RenderUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.phys.AABB;

import java.util.Random;

/**
 * Created 6/9/2021 by SuperMartijn642
 */
public class CollectorTileRenderer implements BlockEntityRenderer<CollectorTile> {

    @Override
    public void render(CollectorTile tile, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
        if(tile.showArea){
            matrixStack.pushPose();
            matrixStack.translate(-tile.getBlockPos().getX(), -tile.getBlockPos().getY(), -tile.getBlockPos().getZ());

            AABB area = tile.getAffectedArea().inflate(0.05f);

            Random random = new Random(tile.getBlockPos().hashCode());
            float red = random.nextFloat();
            float green = random.nextFloat();
            float blue = random.nextFloat();
            float alpha = 0.3f;

            RenderUtils.renderBox(matrixStack, area, red, green, blue);
            RenderUtils.renderBoxSides(matrixStack, area, red, green, blue, alpha);

            matrixStack.popPose();
        }
    }
}
