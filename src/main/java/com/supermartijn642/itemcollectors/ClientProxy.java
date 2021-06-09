package com.supermartijn642.itemcollectors;

import com.supermartijn642.itemcollectors.screen.AdvancedCollectorContainer;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorScreen;
import net.minecraft.client.gui.ScreenManager;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class ClientProxy {

    public static void registerScreen(){
        ScreenManager.registerFactory(ItemCollectors.advanced_collector_container, (ScreenManager.IScreenFactory<AdvancedCollectorContainer,AdvancedCollectorScreen>)(container, inventory, title) -> new AdvancedCollectorScreen(container));
    }

}
