package com.supermartijn642.itemcollectors;

import com.supermartijn642.itemcollectors.screen.AdvancedCollectorContainer;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorScreen;
import com.supermartijn642.itemcollectors.screen.BasicCollectorContainer;
import com.supermartijn642.itemcollectors.screen.BasicCollectorScreen;
import net.minecraft.client.gui.ScreenManager;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class ClientProxy {

    public static void registerScreen(){
        ScreenManager.registerFactory(ItemCollectors.basic_collector_container, (ScreenManager.IScreenFactory<BasicCollectorContainer,BasicCollectorScreen>)(container, inventory, title) -> new BasicCollectorScreen(container));
        ScreenManager.registerFactory(ItemCollectors.advanced_collector_container, (ScreenManager.IScreenFactory<AdvancedCollectorContainer,AdvancedCollectorScreen>)(container, inventory, title) -> new AdvancedCollectorScreen(container));
    }

}
