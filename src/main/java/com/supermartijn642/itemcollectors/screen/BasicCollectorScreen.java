package com.supermartijn642.itemcollectors.screen;

import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.TileEntityBaseScreen;
import com.supermartijn642.itemcollectors.CollectorTile;
import com.supermartijn642.itemcollectors.ItemCollectors;
import com.supermartijn642.itemcollectors.packet.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class BasicCollectorScreen extends TileEntityBaseScreen<CollectorTile> {

    private ShowAreaButton showAreaButton;

    public BasicCollectorScreen(BlockPos pos){
        super(new TextComponentString(""), pos);
    }

    @Override
    protected float sizeX(CollectorTile tile){
        return 202;
    }

    @Override
    protected float sizeY(CollectorTile tile){
        return 82;
    }

    @Override
    protected void addWidgets(CollectorTile tile){
        this.addWidget(new ArrowButton(30, 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseXRange(this.tilePos))));
        this.addWidget(new ArrowButton(30, 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseXRange(this.tilePos))));
        this.addWidget(new ArrowButton(73, 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseYRange(this.tilePos))));
        this.addWidget(new ArrowButton(73, 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseYRange(this.tilePos))));
        this.addWidget(new ArrowButton(116, 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseZRange(this.tilePos))));
        this.addWidget(new ArrowButton(116, 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseZRange(this.tilePos))));
        this.showAreaButton = this.addWidget(new ShowAreaButton(160, 45, () -> ItemCollectors.CHANNEL.sendToServer(new PacketToggleShowArea(this.tilePos))));
        this.showAreaButton.update(tile.showArea);
    }

    @Override
    protected void tick(CollectorTile tile){
        this.showAreaButton.update(tile.showArea);
    }

    @Override
    protected void render(int mouseX, int mouseY, CollectorTile tile){
        this.drawScreenBackground();

        ScreenUtils.drawCenteredString(tile.getBlockState().getBlock().getLocalizedName(), this.sizeX(tile) / 2f, 6);

        ScreenUtils.drawString(new TextComponentTranslation("gui.itemcollectors.basic_collector.range",
            (tile.rangeX * 2 + 1), (tile.rangeY * 2 + 1), (tile.rangeZ * 2 + 1)), 8, 26);
        ScreenUtils.drawCenteredString(new TextComponentString("x:"), 25, 51);
        ScreenUtils.drawCenteredString(new TextComponentString("" + tile.rangeX), 39, 52);
        ScreenUtils.drawCenteredString(new TextComponentString("y:"), 68, 51);
        ScreenUtils.drawCenteredString(new TextComponentString("" + tile.rangeY), 82, 52);
        ScreenUtils.drawCenteredString(new TextComponentString("z:"), 111, 51);
        ScreenUtils.drawCenteredString(new TextComponentString("" + tile.rangeZ), 125, 52);

        GlStateManager.enableAlpha();
    }
}
