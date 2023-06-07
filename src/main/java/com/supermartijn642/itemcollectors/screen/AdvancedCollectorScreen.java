package com.supermartijn642.itemcollectors.screen;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.BlockEntityBaseContainerWidget;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
import com.supermartijn642.itemcollectors.CollectorBlockEntity;
import com.supermartijn642.itemcollectors.ItemCollectors;
import com.supermartijn642.itemcollectors.packet.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class AdvancedCollectorScreen extends BlockEntityBaseContainerWidget<CollectorBlockEntity,AdvancedCollectorContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation("itemcollectors", "textures/filter_screen.png");

    private WhitelistButton whitelistButton;
    private DurabilityButton durabilityButton;
    private ShowAreaButton showAreaButton;

    public AdvancedCollectorScreen(Level level, BlockPos pos){
        super(0, 0, 224, 206, level, pos);
    }

    @Override
    protected Component getNarrationMessage(CollectorBlockEntity entity){
        return TextComponents.blockState(entity.getBlockState()).get();
    }

    @Override
    protected void addWidgets(CollectorBlockEntity entity){
        this.addWidget(new ArrowButton(30, 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseXRange(this.object.getBlockPos()))));
        this.addWidget(new ArrowButton(30, 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseXRange(this.object.getBlockPos()))));
        this.addWidget(new ArrowButton(73, 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseYRange(this.object.getBlockPos()))));
        this.addWidget(new ArrowButton(73, 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseYRange(this.object.getBlockPos()))));
        this.addWidget(new ArrowButton(116, 37, false, () -> ItemCollectors.CHANNEL.sendToServer(new PacketIncreaseZRange(this.object.getBlockPos()))));
        this.addWidget(new ArrowButton(116, 63, true, () -> ItemCollectors.CHANNEL.sendToServer(new PacketDecreaseZRange(this.object.getBlockPos()))));
        this.whitelistButton = this.addWidget(new WhitelistButton(175, 88, () -> ItemCollectors.CHANNEL.sendToServer(new PacketToggleWhitelist(this.object.getBlockPos()))));
        this.whitelistButton.update(entity.filterWhitelist);
        this.durabilityButton = this.addWidget(new DurabilityButton(197, 88, () -> ItemCollectors.CHANNEL.sendToServer(new PacketToggleDurability(this.object.getBlockPos()))));
        this.durabilityButton.update(entity.filterDurability);
        this.showAreaButton = this.addWidget(new ShowAreaButton(160, 45, () -> ItemCollectors.CHANNEL.sendToServer(new PacketToggleShowArea(this.object.getBlockPos()))));
        this.showAreaButton.update(entity.showArea);
        super.addWidgets(entity);
    }

    @Override
    protected void update(CollectorBlockEntity entity){
        this.whitelistButton.update(entity.filterWhitelist);
        this.durabilityButton.update(entity.filterDurability);
        this.showAreaButton.update(entity.showArea);
        super.update(entity);
    }

    @Override
    protected void renderBackground(WidgetRenderContext context, int mouseX, int mouseY, CollectorBlockEntity entity){
        ScreenUtils.bindTexture(BACKGROUND);
        ScreenUtils.drawTexture(context.poseStack(), 0, 0, this.width(), this.height());
        super.renderBackground(context, mouseX, mouseY, entity);
    }

    @Override
    protected void renderForeground(WidgetRenderContext context, int mouseX, int mouseY, CollectorBlockEntity entity){
        ScreenUtils.drawCenteredString(context.poseStack(), entity.getBlockState().getBlock().getName(), this.width() / 2f, 6);
        ScreenUtils.drawString(context.poseStack(), ClientUtils.getPlayer().getInventory().getName(), 32, 112);

        ScreenUtils.drawString(context.poseStack(), TextComponents.translation("gui.itemcollectors.basic_collector.range",
            (entity.rangeX * 2 + 1), (entity.rangeY * 2 + 1), (entity.rangeZ * 2 + 1)).get(), 8, 26);
        ScreenUtils.drawCenteredString(context.poseStack(), TextComponents.string("x:").get(), 25, 51);
        ScreenUtils.drawCenteredString(context.poseStack(), TextComponents.number(entity.rangeX).get(), 39, 52);
        ScreenUtils.drawCenteredString(context.poseStack(), TextComponents.string("y:").get(), 68, 51);
        ScreenUtils.drawCenteredString(context.poseStack(), TextComponents.number(entity.rangeY).get(), 82, 52);
        ScreenUtils.drawCenteredString(context.poseStack(), TextComponents.string("z:").get(), 111, 51);
        ScreenUtils.drawCenteredString(context.poseStack(), TextComponents.number(entity.rangeZ).get(), 125, 52);
        ScreenUtils.drawString(context.poseStack(), TextComponents.translation("gui.itemcollectors.advanced_collector.filter").get(), 8, 78);
        super.renderForeground(context, mouseX, mouseY, entity);
    }
}
