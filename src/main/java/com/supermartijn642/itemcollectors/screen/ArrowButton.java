package com.supermartijn642.itemcollectors.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class ArrowButton extends AbstractButton {

    private final ResourceLocation BUTTONS = new ResourceLocation("itemcollectors", "textures/arrow_buttons.png");

    private final boolean down;
    private final Runnable onPress;

    public ArrowButton(int x, int y, boolean down, Runnable onPress){
        super(x, y, 17, 11, new StringTextComponent(""));
        this.down = down;
        this.onPress = onPress;
    }

    @Override
    public void func_230930_b_(){
        this.onPress.run();
    }

    @Override
    public void func_230431_b_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(BUTTONS);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.field_230695_q_);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        drawTexture(this.field_230690_l_, this.field_230691_m_, this.down ? 17 : 0, (this.field_230693_o_ ? this.field_230692_n_ ? 1 : 0 : 2) * 11);
        this.func_230441_a_(matrixStack, minecraft, mouseX, mouseY);
    }

    private static void drawTexture(int x, int y, int textureX, int textureY){
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + 11, 0).tex(textureX / 34f, (textureY + 11) / 33f).endVertex();
        bufferbuilder.pos(x + 17, y + 11, 0).tex((textureX + 17) / 34f, (textureY + 11) / 33f).endVertex();
        bufferbuilder.pos(x + 17, y, 0).tex((textureX + 17) / 34f, textureY / 33f).endVertex();
        bufferbuilder.pos(x, y, 0).tex(textureX / 34f, textureY / 33f).endVertex();
        bufferbuilder.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(bufferbuilder);
    }
}
