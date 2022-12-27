package com.supermartijn642.itemcollectors.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.BlockEntityBaseWidget;
import com.supermartijn642.itemcollectors.CollectorBlockEntity;
import com.supermartijn642.itemcollectors.ItemCollectors;
import com.supermartijn642.itemcollectors.packet.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class BasicCollectorScreen extends BlockEntityBaseWidget<CollectorBlockEntity> {

    private ShowAreaButton showAreaButton;

    public BasicCollectorScreen(Level level, BlockPos pos){
        super(0, 0, 202, 82, level, pos);
    }

    @Override
    protected Component getNarrationMessage(CollectorBlockEntity entity){
        return TextComponents.blockState(entity.getBlockState()).get();
    }

    @Override
    protected void addWidgets(CollectorBlockEntity entity){
        this.addWidget(new ArrowButton(30, 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseXRange(this.blockEntityPos))));
        this.addWidget(new ArrowButton(30, 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseXRange(this.blockEntityPos))));
        this.addWidget(new ArrowButton(73, 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseYRange(this.blockEntityPos))));
        this.addWidget(new ArrowButton(73, 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseYRange(this.blockEntityPos))));
        this.addWidget(new ArrowButton(116, 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseZRange(this.blockEntityPos))));
        this.addWidget(new ArrowButton(116, 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseZRange(this.blockEntityPos))));
        this.showAreaButton = this.addWidget(new ShowAreaButton(160, 45, () -> ItemCollectors.CHANNEL.sendToServer(new PacketToggleShowArea(this.blockEntityPos))));
        this.showAreaButton.update(entity.showArea);
        super.addWidgets(entity);
    }

    @Override
    protected void update(CollectorBlockEntity entity){
        this.showAreaButton.update(entity.showArea);
        super.update(entity);
    }

    @Override
    protected void render(PoseStack poseStack, int mouseX, int mouseY, CollectorBlockEntity entity){
        ScreenUtils.drawScreenBackground(poseStack, 0, 0, this.width(), this.height());

        ScreenUtils.drawCenteredString(poseStack, TextComponents.blockState(entity.getBlockState()).get(), this.width() / 2f, 6);

        ScreenUtils.drawString(poseStack, TextComponents.translation("gui.itemcollectors.basic_collector.range",
            (entity.rangeX * 2 + 1), (entity.rangeY * 2 + 1), (entity.rangeZ * 2 + 1)).get(), 8, 26);
        ScreenUtils.drawCenteredString(poseStack, TextComponents.string("x:").get(), 25, 51);
        ScreenUtils.drawCenteredString(poseStack, TextComponents.number(entity.rangeX).get(), 39, 52);
        ScreenUtils.drawCenteredString(poseStack, TextComponents.string("y:").get(), 68, 51);
        ScreenUtils.drawCenteredString(poseStack, TextComponents.number(entity.rangeY).get(), 82, 52);
        ScreenUtils.drawCenteredString(poseStack, TextComponents.string("z:").get(), 111, 51);
        ScreenUtils.drawCenteredString(poseStack, TextComponents.number(entity.rangeZ).get(), 125, 52);
        super.render(poseStack, mouseX, mouseY, entity);
    }
}
