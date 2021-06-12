package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.render.RenderUtils;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorContainer;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorScreen;
import com.supermartijn642.itemcollectors.screen.BasicCollectorScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy {

    @SubscribeEvent
    public static void onSetup(FMLClientSetupEvent e){
        ScreenManager.registerFactory(ItemCollectors.advanced_collector_container, (ScreenManager.IScreenFactory<AdvancedCollectorContainer,AdvancedCollectorScreen>)(container, inventory, title) -> new AdvancedCollectorScreen(container));
        ClientRegistry.bindTileEntityRenderer(ItemCollectors.basic_collector_tile, CollectorTileRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ItemCollectors.advanced_collector_tile, CollectorTileRenderer::new);
    }

    @Mod.EventBusSubscriber(Dist.CLIENT)
    public static class Events {

        @SubscribeEvent
        public static void onBlockHighlight(DrawHighlightEvent.HighlightBlock e){
            World world = ClientUtils.getWorld();
            TileEntity tile = world.getTileEntity(e.getTarget().getPos());
            if(tile instanceof CollectorTile)
                RenderUtils.renderBox(e.getMatrix(), ((CollectorTile)tile).getAffectedArea(), 245 / 255f, 212 / 255f, 66 / 255f);
        }
    }

    public static void openBasicCollectorScreen(BlockPos pos){
        ClientUtils.displayScreen(new BasicCollectorScreen(pos));
    }

}
