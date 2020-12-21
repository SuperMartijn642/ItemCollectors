package com.supermartijn642.itemcollectors.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.supermartijn642.itemcollectors.CollectorTile;
import com.supermartijn642.itemcollectors.ItemCollectors;
import com.supermartijn642.itemcollectors.packet.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class AdvancedCollectorScreen extends CollectorScreen<AdvancedCollectorContainer> {

    private ArrowButton upXButton, downXButton;
    private ArrowButton upYButton, downYButton;
    private ArrowButton upZButton, downZButton;
    private WhitelistButton whitelistButton;
    private DurabilityButton durabilityButton;

    public AdvancedCollectorScreen(AdvancedCollectorContainer container){
        super(container, ItemCollectors.advanced_collector.getTranslationKey());
    }

    @Override
    protected void addButtons(CollectorTile tile){
        this.upXButton = this.addButton(new ArrowButton(this.guiLeft + 40, this.guiTop + 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseXRange(this.container.pos))));
        this.downXButton = this.addButton(new ArrowButton(this.guiLeft + 40, this.guiTop + 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseXRange(this.container.pos))));
        this.upYButton = this.addButton(new ArrowButton(this.guiLeft + 93, this.guiTop + 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseYRange(this.container.pos))));
        this.downYButton = this.addButton(new ArrowButton(this.guiLeft + 93, this.guiTop + 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseYRange(this.container.pos))));
        this.upZButton = this.addButton(new ArrowButton(this.guiLeft + 146, this.guiTop + 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseZRange(this.container.pos))));
        this.downZButton = this.addButton(new ArrowButton(this.guiLeft + 146, this.guiTop + 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseZRange(this.container.pos))));
        this.whitelistButton = this.addButton(new WhitelistButton(this.guiLeft + 175, this.guiTop + 88, () -> ItemCollectors.CHANNEL.sendToServer(new PacketToggleWhitelist(this.container.pos))));
        this.whitelistButton.update(tile.filterWhitelist);
        this.durabilityButton = this.addButton(new DurabilityButton(this.guiLeft + 197, this.guiTop + 88, () -> ItemCollectors.CHANNEL.sendToServer(new PacketToggleDurability(this.container.pos))));
        this.durabilityButton.update(tile.filterDurability);
    }

    @Override
    protected void drawToolTips(MatrixStack matrixStack, CollectorTile tile, int mouseX, int mouseY){
        if(this.upXButton.isHovered() || this.upYButton.isHovered() || this.upZButton.isHovered())
            this.renderToolTip(matrixStack, true, "gui.itemcollectors.basic_collector.range.increase", mouseX, mouseY);
        if(this.downXButton.isHovered() || this.downYButton.isHovered() || this.downZButton.isHovered())
            this.renderToolTip(matrixStack, true, "gui.itemcollectors.basic_collector.range.decrease", mouseX, mouseY);
        if(this.whitelistButton.isHovered())
            this.renderToolTip(matrixStack, true, "gui.itemcollectors.advanced_collector.whitelist." + (tile.filterWhitelist ? "on" : "off"), mouseX, mouseY);
        if(this.durabilityButton.isHovered())
            this.renderToolTip(matrixStack, true, "gui.itemcollectors.advanced_collector.durability." + (tile.filterDurability ? "on" : "off"), mouseX, mouseY);
    }

    @Override
    protected void tick(CollectorTile tile){
        this.whitelistButton.update(tile.filterWhitelist);
        this.durabilityButton.update(tile.filterDurability);
    }

    @Override
    protected String getBackground(){
        return "advanced_collector_screen.png";
    }

    @Override
    protected void drawText(MatrixStack matrixStack, CollectorTile tile){
        this.drawCenteredString(matrixStack, this.title, this.xSize / 2f, 6);
        this.drawString(matrixStack, this.playerInventory.getDisplayName(), 32, 112);

        String range = I18n.format("gui.itemcollectors.basic_collector.range")
            .replace("$numberx$", "" + (tile.rangeX * 2 + 1))
            .replace("$numbery$", "" + (tile.rangeY * 2 + 1))
            .replace("$numberz$", "" + (tile.rangeZ * 2 + 1));
        this.drawString(matrixStack, new StringTextComponent(range), 8, 26);
        this.drawCenteredString(matrixStack, new StringTextComponent("x:"), 35, 51);
        this.drawCenteredString(matrixStack, new StringTextComponent("" + tile.rangeX), 49, 52);
        this.drawCenteredString(matrixStack, new StringTextComponent("y:"), 88, 51);
        this.drawCenteredString(matrixStack, new StringTextComponent("" + tile.rangeY), 102, 52);
        this.drawCenteredString(matrixStack, new StringTextComponent("z:"), 141, 51);
        this.drawCenteredString(matrixStack, new StringTextComponent("" + tile.rangeZ), 155, 52);
        this.drawString(matrixStack, new TranslationTextComponent("gui.itemcollectors.advanced_collector.filter"), 8, 78);
    }
}
