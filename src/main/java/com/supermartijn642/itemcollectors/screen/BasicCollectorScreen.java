package com.supermartijn642.itemcollectors.screen;

import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.TileEntityBaseScreen;
import com.supermartijn642.itemcollectors.CollectorTile;
import com.supermartijn642.itemcollectors.ItemCollectors;
import com.supermartijn642.itemcollectors.packet.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class BasicCollectorScreen extends TileEntityBaseScreen<CollectorTile> {

    public BasicCollectorScreen(BlockPos pos){
        super(new TextComponentString(ItemCollectors.basic_collector.getLocalizedName()), pos);
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
        this.addWidget(new ArrowButton(40, 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseXRange(this.tilePos))));
        this.addWidget(new ArrowButton(40, 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseXRange(this.tilePos))));
        this.addWidget(new ArrowButton(93, 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseYRange(this.tilePos))));
        this.addWidget(new ArrowButton(93, 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseYRange(this.tilePos))));
        this.addWidget(new ArrowButton(146, 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseZRange(this.tilePos))));
        this.addWidget(new ArrowButton(146, 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseZRange(this.tilePos))));
    }

    @Override
    protected void render(int mouseX, int mouseY, CollectorTile tile){
        this.drawScreenBackground();

        ScreenUtils.drawCenteredString(this.title, this.sizeX(tile) / 2f, 6);
        String range = I18n.format("gui.itemcollectors.basic_collector.range")
            .replace("$numberx$", "" + (tile.rangeX * 2 + 1))
            .replace("$numbery$", "" + (tile.rangeY * 2 + 1))
            .replace("$numberz$", "" + (tile.rangeZ * 2 + 1));
        ScreenUtils.drawString(new TextComponentString(range), 8, 26);

        ScreenUtils.drawCenteredString(new TextComponentString("x:"), 35, 51);
        ScreenUtils.drawCenteredString(new TextComponentString("" + tile.rangeX), 49, 52);
        ScreenUtils.drawCenteredString(new TextComponentString("y:"), 88, 51);
        ScreenUtils.drawCenteredString(new TextComponentString("" + tile.rangeY), 102, 52);
        ScreenUtils.drawCenteredString(new TextComponentString("z:"), 141, 51);
        ScreenUtils.drawCenteredString(new TextComponentString("" + tile.rangeZ), 155, 52);

        GlStateManager.enableAlpha();
    }
}
