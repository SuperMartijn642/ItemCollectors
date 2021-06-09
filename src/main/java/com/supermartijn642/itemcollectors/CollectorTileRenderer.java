package com.supermartijn642.itemcollectors;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * Created 6/9/2021 by SuperMartijn642
 */
public class CollectorTileRenderer extends TileEntityRenderer<CollectorTile> {

    @Override
    public void render(CollectorTile tile, double x, double y, double z, float partialTicks, int destroyStage){
        if(!tile.showArea)
            return;

        GlStateManager.disableTexture();
        GlStateManager.disableLighting();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.setTranslation(x, y, z);

        builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        ClientProxy.drawShape(builder, tile.getAffectedArea().offset(-tile.getPos().getX(), -tile.getPos().getY(), -tile.getPos().getZ()), 245 / 255f, 212 / 255f, 66 / 255f, 1);

        builder.setTranslation(0, 0, 0);
        tessellator.draw();

        GlStateManager.enableTexture();
        GlStateManager.enableLighting();
    }
}
