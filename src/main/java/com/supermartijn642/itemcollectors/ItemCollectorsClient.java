package com.supermartijn642.itemcollectors;

import com.mojang.blaze3d.platform.GlStateManager;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.gui.WidgetContainerScreen;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.registry.ClientRegistrationHandler;
import com.supermartijn642.core.render.RenderUtils;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorScreen;
import com.supermartijn642.itemcollectors.screen.BasicCollectorScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class ItemCollectorsClient {

    public static void register(){
        ClientRegistrationHandler handler = ClientRegistrationHandler.get("itemcollectors");
        handler.registerContainerScreen(() -> ItemCollectors.filter_collector_container, container -> WidgetContainerScreen.of(new AdvancedCollectorScreen(container.level, container.getCollectorPosition()), container, false));
        handler.registerCustomBlockEntityRenderer(() -> ItemCollectors.basic_collector_tile, CollectorBlockEntityRenderer::new);
        handler.registerCustomBlockEntityRenderer(() -> ItemCollectors.advanced_collector_tile, CollectorBlockEntityRenderer::new);
    }

    public static void openBasicCollectorScreen(World level, BlockPos pos){
        ClientUtils.displayScreen(WidgetScreen.of(new BasicCollectorScreen(level, pos)));
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Events {

        @SubscribeEvent
        public static void onBlockHighlight(DrawBlockHighlightEvent.HighlightBlock e){
            World level = ClientUtils.getWorld();
            TileEntity entity = level.getBlockEntity(e.getTarget().getBlockPos());
            if(entity instanceof CollectorBlockEntity){
                GlStateManager.pushMatrix();
                Vec3d camera = RenderUtils.getCameraPosition();
                GlStateManager.translated(-camera.x, -camera.y, -camera.z);

                AxisAlignedBB area = ((CollectorBlockEntity)entity).getAffectedArea().inflate(0.05f);

                Random random = new Random(entity.getBlockPos().hashCode());
                float red = random.nextFloat();
                float green = random.nextFloat();
                float blue = random.nextFloat();
                float alpha = 0.3f;

                RenderUtils.renderBox(area, red, green, blue, true);
                RenderUtils.renderBoxSides(area, red, green, blue, alpha, true);

                GlStateManager.popMatrix();
            }
        }
    }
}
