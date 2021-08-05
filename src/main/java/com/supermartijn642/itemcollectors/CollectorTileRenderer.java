package com.supermartijn642.itemcollectors;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.render.RenderUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.phys.Vec3;

/**
 * Created 6/9/2021 by SuperMartijn642
 */
public class CollectorTileRenderer implements BlockEntityRenderer<CollectorTile> {

    @Override
    public void render(CollectorTile tile, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
        if(tile.showArea){
            matrixStack.pushPose();
            Vec3 camera = RenderUtils.getCameraPosition();
            matrixStack.translate(camera.x, camera.y, camera.z);
            matrixStack.translate(-tile.getBlockPos().getX(), -tile.getBlockPos().getY(), -tile.getBlockPos().getZ());
            RenderUtils.renderBox(matrixStack, tile.getAffectedArea(), 245 / 255f, 212 / 255f, 66 / 255f);
            matrixStack.popPose();
        }
    }
}
