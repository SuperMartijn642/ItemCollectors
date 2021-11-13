package com.supermartijn642.itemcollectors;

import com.mojang.blaze3d.platform.GlStateManager;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.render.RenderUtils;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorContainer;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorScreen;
import com.supermartijn642.itemcollectors.screen.BasicCollectorScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
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
        ScreenManager.registerFactory(ItemCollectors.advanced_collector_container, (ScreenManager.IScreenFactory<AdvancedCollectorContainer,AdvancedCollectorScreen>)(container, inventory, title) -> new AdvancedCollectorScreen(container));
        ClientRegistry.bindTileEntitySpecialRenderer(CollectorTile.class, new CollectorTileRenderer());
    }

    @Mod.EventBusSubscriber(Dist.CLIENT)
    public static class Events {

        @SubscribeEvent
        public static void onBlockHighlight(DrawBlockHighlightEvent.HighlightBlock e){
            World world = ClientUtils.getWorld();
            TileEntity tile = world.getTileEntity(e.getTarget().getPos());
            if(tile instanceof CollectorTile){
                GlStateManager.pushMatrix();
                Vec3d camera = RenderUtils.getCameraPosition();
                GlStateManager.translated(-camera.x, -camera.y, -camera.z);

                AxisAlignedBB area = ((CollectorTile)tile).getAffectedArea().grow(0.05f);

                Random random = new Random(tile.getPos().hashCode());
                float red = random.nextFloat();
                float green = random.nextFloat();
                float blue = random.nextFloat();
                float alpha = 0.3f;

                RenderUtils.renderBox(area, red, green, blue);
                RenderUtils.renderBoxSides(area, red, green, blue, alpha);

                GlStateManager.popMatrix();
            }
        }
    }

    public static void openBasicCollectorScreen(BlockPos pos){
        ClientUtils.displayScreen(new BasicCollectorScreen(pos));
    }

}
