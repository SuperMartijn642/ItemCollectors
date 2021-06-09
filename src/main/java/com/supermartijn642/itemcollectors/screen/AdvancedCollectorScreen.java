package com.supermartijn642.itemcollectors.screen;

import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.TileEntityBaseContainerScreen;
import com.supermartijn642.itemcollectors.CollectorTile;
import com.supermartijn642.itemcollectors.ItemCollectors;
import com.supermartijn642.itemcollectors.packet.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class AdvancedCollectorScreen extends TileEntityBaseContainerScreen<CollectorTile,AdvancedCollectorContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation("itemcollectors", "textures/filter_screen.png");

    private WhitelistButton whitelistButton;
    private DurabilityButton durabilityButton;

    public AdvancedCollectorScreen(AdvancedCollectorContainer container){
        super(container, ItemCollectors.advanced_collector.getNameTextComponent());
    }

    @Override
    protected int sizeX(CollectorTile tile){
        return 224;
    }

    @Override
    protected int sizeY(CollectorTile tile){
        return 206;
    }

    @Override
    protected void addWidgets(CollectorTile tile){
        this.addWidget(new ArrowButton(40, 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseXRange(this.container.getTilePos()))));
        this.addWidget(new ArrowButton(40, 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseXRange(this.container.getTilePos()))));
        this.addWidget(new ArrowButton(93, 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseYRange(this.container.getTilePos()))));
        this.addWidget(new ArrowButton(93, 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseYRange(this.container.getTilePos()))));
        this.addWidget(new ArrowButton(146, 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseZRange(this.container.getTilePos()))));
        this.addWidget(new ArrowButton(146, 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseZRange(this.container.getTilePos()))));
        this.whitelistButton = this.addWidget(new WhitelistButton(175, 88, () -> ItemCollectors.CHANNEL.sendToServer(new PacketToggleWhitelist(this.container.getTilePos()))));
        this.whitelistButton.update(tile.filterWhitelist);
        this.durabilityButton = this.addWidget(new DurabilityButton(197, 88, () -> ItemCollectors.CHANNEL.sendToServer(new PacketToggleDurability(this.container.getTilePos()))));
        this.durabilityButton.update(tile.filterDurability);
    }

    @Override
    protected void tick(CollectorTile tile){
        this.whitelistButton.update(tile.filterWhitelist);
        this.durabilityButton.update(tile.filterDurability);
    }

    @Override
    protected void renderBackground(int mouseX, int mouseY, CollectorTile tile){
        ScreenUtils.bindTexture(BACKGROUND);
        ScreenUtils.drawTexture(0, 0, this.sizeX(), this.sizeY());
    }

    @Override
    protected void renderForeground(int mouseX, int mouseY, CollectorTile tile){
        ScreenUtils.drawCenteredString(this.title, this.sizeX() / 2f, 6);
        ScreenUtils.drawString(this.playerInventory.getDisplayName(), 32, 112);

        ScreenUtils.drawString(new TranslationTextComponent("gui.itemcollectors.basic_collector.range",
            (tile.rangeX * 2 + 1), (tile.rangeY * 2 + 1), (tile.rangeZ * 2 + 1)), 8, 26);
        ScreenUtils.drawCenteredString(new StringTextComponent("x:"), 35, 51);
        ScreenUtils.drawCenteredString(new StringTextComponent("" + tile.rangeX), 49, 52);
        ScreenUtils.drawCenteredString(new StringTextComponent("y:"), 88, 51);
        ScreenUtils.drawCenteredString(new StringTextComponent("" + tile.rangeY), 102, 52);
        ScreenUtils.drawCenteredString(new StringTextComponent("z:"), 141, 51);
        ScreenUtils.drawCenteredString(new StringTextComponent("" + tile.rangeZ), 155, 52);
        ScreenUtils.drawString(new TranslationTextComponent("gui.itemcollectors.advanced_collector.filter"), 8, 78);
    }
}
