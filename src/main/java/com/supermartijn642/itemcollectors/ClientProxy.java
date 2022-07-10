package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.render.RenderUtils;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorContainer;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorScreen;
import com.supermartijn642.itemcollectors.screen.BasicCollectorScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
        MenuScreens.register(ItemCollectors.advanced_collector_container, (MenuScreens.ScreenConstructor<AdvancedCollectorContainer,AdvancedCollectorScreen>)(container, inventory, title) -> new AdvancedCollectorScreen(container));
        BlockEntityRenderers.register(ItemCollectors.basic_collector_tile, context -> new CollectorTileRenderer());
        BlockEntityRenderers.register(ItemCollectors.advanced_collector_tile, context -> new CollectorTileRenderer());
    }

    @Mod.EventBusSubscriber(Dist.CLIENT)
    public static class Events {

        @SubscribeEvent
        public static void onBlockHighlight(RenderHighlightEvent.Block e){
            Level world = ClientUtils.getWorld();
            BlockEntity tile = world.getBlockEntity(e.getTarget().getBlockPos());
            if(tile instanceof CollectorTile){
                e.getPoseStack().pushPose();
                Vec3 camera = RenderUtils.getCameraPosition();
                e.getPoseStack().translate(-camera.x, -camera.y, -camera.z);

                AABB area = ((CollectorTile)tile).getAffectedArea().inflate(0.05f);

                Random random = new Random(tile.getBlockPos().hashCode());
                float red = random.nextFloat();
                float green = random.nextFloat();
                float blue = random.nextFloat();
                float alpha = 0.3f;

                RenderUtils.renderBox(e.getPoseStack(), area, red, green, blue);
                RenderUtils.renderBoxSides(e.getPoseStack(), area, red, green, blue, alpha);

                e.getPoseStack().popPose();
            }
        }
    }

    public static void openBasicCollectorScreen(BlockPos pos){
        ClientUtils.displayScreen(new BasicCollectorScreen(pos));
    }

}
