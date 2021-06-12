package com.supermartijn642.itemcollectors;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.supermartijn642.core.render.RenderUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.Vec3d;

/**
 * Created 6/9/2021 by SuperMartijn642
 */
public class CollectorTileRenderer extends TileEntityRenderer<CollectorTile> {

    public CollectorTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn){
        super(rendererDispatcherIn);
    }

    @Override
    public void render(CollectorTile tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
        if(tile.showArea){
            matrixStack.push();
            Vec3d camera = RenderUtils.getCameraPosition();
            matrixStack.translate(camera.x, camera.y, camera.z);
            matrixStack.translate(-tile.getPos().getX(), -tile.getPos().getY(), -tile.getPos().getZ());
            RenderUtils.renderBox(matrixStack, tile.getAffectedArea(), 245 / 255f, 212 / 255f, 66 / 255f);
            matrixStack.pop();
        }
    }
}
