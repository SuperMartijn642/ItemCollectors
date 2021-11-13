package com.supermartijn642.itemcollectors;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.supermartijn642.core.render.RenderUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Random;

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
            matrixStack.translate(-tile.getPos().getX(), -tile.getPos().getY(), -tile.getPos().getZ());

            AxisAlignedBB area = tile.getAffectedArea().grow(0.05f);

            Random random = new Random(tile.getPos().hashCode());
            float red = random.nextFloat();
            float green = random.nextFloat();
            float blue = random.nextFloat();
            float alpha = 0.3f;

            RenderUtils.renderBox(matrixStack, area, red, green, blue);
            RenderUtils.renderBoxSides(matrixStack, area, red, green, blue, alpha);

            matrixStack.pop();
        }
    }
}
