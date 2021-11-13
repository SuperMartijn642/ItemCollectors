package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.render.RenderUtils;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorContainer;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorScreen;
import com.supermartijn642.itemcollectors.screen.BasicCollectorScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Random;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy {

    @SubscribeEvent
    public static void onSetup(FMLClientSetupEvent e){
        ScreenManager.register(ItemCollectors.advanced_collector_container, (ScreenManager.IScreenFactory<AdvancedCollectorContainer,AdvancedCollectorScreen>)(container, inventory, title) -> new AdvancedCollectorScreen(container));
        ClientRegistry.bindTileEntityRenderer(ItemCollectors.basic_collector_tile, CollectorTileRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ItemCollectors.advanced_collector_tile, CollectorTileRenderer::new);
    }

    @Mod.EventBusSubscriber(Dist.CLIENT)
    public static class Events {

        @SubscribeEvent
        public static void onBlockHighlight(DrawHighlightEvent.HighlightBlock e){
            World world = ClientUtils.getWorld();
            TileEntity tile = world.getBlockEntity(e.getTarget().getBlockPos());
            if(tile instanceof CollectorTile){
                e.getMatrix().pushPose();
                Vector3d camera = RenderUtils.getCameraPosition();
                e.getMatrix().translate(-camera.x, -camera.y, -camera.z);

                AxisAlignedBB area = ((CollectorTile)tile).getAffectedArea().inflate(0.05f);

                Random random = new Random(tile.getBlockPos().hashCode());
                float red = random.nextFloat();
                float green = random.nextFloat();
                float blue = random.nextFloat();
                float alpha = 0.3f;

                RenderUtils.renderBox(e.getMatrix(), area, red, green, blue);
                RenderUtils.renderBoxSides(e.getMatrix(), area, red, green, blue, alpha);

                e.getMatrix().popPose();
            }
        }
    }

    public static void openBasicCollectorScreen(BlockPos pos){
        ClientUtils.displayScreen(new BasicCollectorScreen(pos));
    }

}
