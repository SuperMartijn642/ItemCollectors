package com.supermartijn642.itemcollectors.screen;

import com.supermartijn642.itemcollectors.CollectorTile;
import com.supermartijn642.itemcollectors.ItemCollectors;
import com.supermartijn642.itemcollectors.packet.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class AdvancedCollectorScreen extends CollectorScreen<AdvancedCollectorContainer> {

    private ArrowButton upXButton, downXButton;
    private ArrowButton upYButton, downYButton;
    private ArrowButton upZButton, downZButton;
    private WhitelistButton whitelistButton;

    public AdvancedCollectorScreen(AdvancedCollectorContainer container){
        super(container, ItemCollectors.advanced_collector.getUnlocalizedName());
    }

    @Override
    protected void addButtons(CollectorTile tile){
        this.upXButton = this.addButton(new ArrowButton(0, this.guiLeft + 40, this.guiTop + 37, false, () -> ItemCollectors.channel.sendToServer(new PacketIncreaseXRange(this.container.pos))));
        this.downXButton = this.addButton(new ArrowButton(1, this.guiLeft + 40, this.guiTop + 63, true, () -> ItemCollectors.channel.sendToServer(new PacketDecreaseXRange(this.container.pos))));
        this.upYButton = this.addButton(new ArrowButton(2, this.guiLeft + 93, this.guiTop + 37, false, () -> ItemCollectors.channel.sendToServer(new PacketIncreaseYRange(this.container.pos))));
        this.downYButton = this.addButton(new ArrowButton(3, this.guiLeft + 93, this.guiTop + 63, true, () -> ItemCollectors.channel.sendToServer(new PacketDecreaseYRange(this.container.pos))));
        this.upZButton = this.addButton(new ArrowButton(4, this.guiLeft + 146, this.guiTop + 37, false, () -> ItemCollectors.channel.sendToServer(new PacketIncreaseZRange(this.container.pos))));
        this.downZButton = this.addButton(new ArrowButton(5, this.guiLeft + 146, this.guiTop + 63, true, () -> ItemCollectors.channel.sendToServer(new PacketDecreaseZRange(this.container.pos))));
        this.whitelistButton = this.addButton(new WhitelistButton(6, this.guiLeft + 175, this.guiTop + 88, () -> ItemCollectors.channel.sendToServer(new PacketToggleWhitelist(this.container.pos))));
        this.whitelistButton.update(tile.filterWhitelist);
    }

    @Override
    protected void drawToolTips(CollectorTile tile, int mouseX, int mouseY){
        if(this.upXButton.isMouseOver() || this.upYButton.isMouseOver() || this.upZButton.isMouseOver())
            this.renderToolTip(true, "gui.itemcollectors.basic_collector.range.increase", mouseX, mouseY);
        if(this.downXButton.isMouseOver() || this.downYButton.isMouseOver() || this.downZButton.isMouseOver())
            this.renderToolTip(true, "gui.itemcollectors.basic_collector.range.decrease", mouseX, mouseY);
        if(this.whitelistButton.isMouseOver())
            this.renderToolTip(true, "gui.itemcollectors.advanced_collector.whitelist.on", mouseX, mouseY);
    }

    @Override
    protected void tick(CollectorTile tile){
        this.whitelistButton.update(tile.filterWhitelist);
    }

    @Override
    protected String getBackground(){
        return "advanced_collector_screen.png";
    }

    @Override
    protected void drawText(CollectorTile tile){
        this.drawCenteredString(this.title, this.xSize / 2, 6);
        this.drawString(this.container.player.inventory.getDisplayName(), 21, 112);

        String range = I18n.format("gui.itemcollectors.basic_collector.range")
            .replace("$numberx$", "" + (tile.rangeX * 2 + 1))
            .replace("$numbery$", "" + (tile.rangeY * 2 + 1))
            .replace("$numberz$", "" + (tile.rangeZ * 2 + 1));
        this.drawString(new TextComponentString(range), 8, 26);
        this.drawCenteredString("x:", 35, 51);
        this.drawCenteredString("" + tile.rangeX, 49, 52);
        this.drawCenteredString("y:", 88, 51);
        this.drawCenteredString("" + tile.rangeY, 102, 52);
        this.drawCenteredString("z:", 141, 51);
        this.drawCenteredString("" + tile.rangeZ, 155, 52);
        this.drawString(new TextComponentTranslation("gui.itemcollectors.advanced_collector.filter"), 8, 78);
    }
}
