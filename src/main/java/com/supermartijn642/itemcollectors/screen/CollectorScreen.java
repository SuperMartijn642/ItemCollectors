package com.supermartijn642.itemcollectors.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.supermartijn642.itemcollectors.CollectorTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public abstract class CollectorScreen<T extends CollectorContainer> extends ContainerScreen<T> {

    public CollectorScreen(T container, String title){
        super(container, container.player.inventory, new TranslationTextComponent(title));
        this.xSize = container.width;
        this.ySize = container.height;
    }

    @Override
    protected void init(){
        super.init();

        CollectorTile tile = this.container.getTileOrClose();
        if(tile != null)
            this.addButtons(tile);
    }

    protected abstract void addButtons(CollectorTile tile);

    @Override
    public void render(int mouseX, int mouseY, float partialTicks){
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        CollectorTile tile = this.container.getTileOrClose();
        if(tile != null)
            this.drawToolTips(tile, mouseX, mouseY);
    }

    protected abstract void drawToolTips(CollectorTile tile, int mouseX, int mouseY);

    @Override
    public void tick(){
        CollectorTile tile = this.container.getTileOrClose();
        if(tile == null)
            return;

        super.tick();
        this.tick(tile);
    }

    protected abstract void tick(CollectorTile tile);

    protected abstract String getBackground();

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation("itemcollectors", "textures/" + this.getBackground()));
        this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        CollectorTile tile = this.container.getTileOrClose();
        if(tile != null)
            this.drawText(tile);
    }

    protected abstract void drawText(CollectorTile tile);

    protected void drawCenteredString(ITextComponent text, float x, float y){
        this.drawCenteredString(text.getFormattedText(), x, y);
    }

    protected void drawCenteredString(String s, float x, float y){
        this.font.drawString(s, this.guiLeft + x - this.font.getStringWidth(s) / 2f, this.guiTop + y, 4210752);
    }

    protected void drawString(ITextComponent text, float x, float y){
        this.drawString(text.getFormattedText(), x, y);
    }

    protected void drawString(String s, float x, float y){
        this.font.drawString(s, this.guiLeft + x, this.guiTop + y, 4210752);
    }

    public void renderToolTip(boolean translate, String string, int x, int y){
        super.renderTooltip(translate ? new TranslationTextComponent(string).getFormattedText() : string, x, y);
    }
}
