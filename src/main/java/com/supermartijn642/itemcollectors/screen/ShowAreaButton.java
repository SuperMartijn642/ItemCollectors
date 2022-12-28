package com.supermartijn642.itemcollectors.screen;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.premade.AbstractButtonWidget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Consumer;

/**
 * Created 7/8/2020 by SuperMartijn642
 */
public class ShowAreaButton extends AbstractButtonWidget {

    private static final ResourceLocation BUTTONS = new ResourceLocation("itemcollectors", "textures/visualize_button.png");

    public boolean on = true;
    public boolean active = true;

    public ShowAreaButton(int x, int y, Runnable onPress){
        super(x, y, 20, 20, onPress);
    }

    public void update(boolean on){
        this.on = on;
    }

    @Override
    public ITextComponent getNarrationMessage(){
        return TextComponents.translation("gui.itemcollectors.advanced_collector.show_area." + (this.on ? "on" : "off")).get();
    }

    @Override
    public void render(int mouseX, int mouseY){
        ScreenUtils.bindTexture(BUTTONS);
        ScreenUtils.drawTexture(this.x, this.y, this.width, this.height, this.on ? 0 : 0.5f, this.active ? this.isFocused() ? 1 / 3f : 0 : 2 / 3f, 0.5f, 1 / 3f);
    }

    @Override
    protected void getTooltips(Consumer<ITextComponent> tooltips){
        tooltips.accept(this.getNarrationMessage());
    }
}
