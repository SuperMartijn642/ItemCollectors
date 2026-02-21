package com.supermartijn642.itemcollectors.screen;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.GuiGraphicsHelper;
import com.supermartijn642.core.gui.widget.BlockEntityBaseWidget;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
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
        this.showAreaButton.update(entity.shouldShowArea());
        super.addWidgets(entity);
    }

    @Override
    protected void update(CollectorBlockEntity entity){
        this.showAreaButton.update(entity.shouldShowArea());
        super.update(entity);
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    protected void render(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY, CollectorBlockEntity entity){
        graphics.submitDefaultScreenBackground(0, 0, this.width(), this.height());

        graphics.submitText(TextComponents.blockState(entity.getBlockState()).get(), this.width() / 2f, 6, p -> p.centerHorizontally());

        graphics.submitText(TextComponents.translation("gui.itemcollectors.basic_collector.range",
            (entity.getRangeX() * 2 + 1), (entity.getRangeY() * 2 + 1), (entity.getRangeZ() * 2 + 1)).get(), 8, 26);
        graphics.submitText(TextComponents.string("x:").get(), 25, 51, p -> p.centerHorizontally());
        graphics.submitText(TextComponents.number(entity.getRangeX()).get(), 39, 52, p -> p.centerHorizontally());
        graphics.submitText(TextComponents.string("y:").get(), 68, 51, p -> p.centerHorizontally());
        graphics.submitText(TextComponents.number(entity.getRangeY()).get(), 82, 52, p -> p.centerHorizontally());
        graphics.submitText(TextComponents.string("z:").get(), 111, 51, p -> p.centerHorizontally());
        graphics.submitText(TextComponents.number(entity.getRangeZ()).get(), 125, 52, p -> p.centerHorizontally());
        super.render(context, graphics, mouseX, mouseY, entity);
    }
}
