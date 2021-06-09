package com.supermartijn642.itemcollectors.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.TileEntityBaseScreen;
import com.supermartijn642.itemcollectors.CollectorTile;
import com.supermartijn642.itemcollectors.ItemCollectors;
import com.supermartijn642.itemcollectors.packet.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class BasicCollectorScreen extends TileEntityBaseScreen<CollectorTile> {

    public BasicCollectorScreen(BlockPos pos){
        super(ItemCollectors.basic_collector.getNameTextComponent(), pos);
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

        ScreenUtils.drawString(new TranslationTextComponent("gui.itemcollectors.basic_collector.range",
            (tile.rangeX * 2 + 1), (tile.rangeY * 2 + 1), (tile.rangeZ * 2 + 1)), 8, 26);
        ScreenUtils.drawCenteredString(new StringTextComponent("x:"), 35, 51);
        ScreenUtils.drawCenteredString(new StringTextComponent("" + tile.rangeX), 49, 52);
        ScreenUtils.drawCenteredString(new StringTextComponent("y:"), 88, 51);
        ScreenUtils.drawCenteredString(new StringTextComponent("" + tile.rangeY), 102, 52);
        ScreenUtils.drawCenteredString(new StringTextComponent("z:"), 141, 51);
        ScreenUtils.drawCenteredString(new StringTextComponent("" + tile.rangeZ), 155, 52);

        GlStateManager.enableAlphaTest();
    }
}
