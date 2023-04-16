package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.gui.WidgetContainerScreen;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.registry.ClientRegistrationHandler;
import com.supermartijn642.core.render.RenderUtils;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorScreen;
import com.supermartijn642.itemcollectors.screen.BasicCollectorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class ItemCollectorsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient(){
        WorldRenderEvents.BLOCK_OUTLINE.register(ItemCollectorsClient::onBlockHighlight);

        register();
    }

    public static void register(){
        ClientRegistrationHandler handler = ClientRegistrationHandler.get("itemcollectors");
        handler.registerContainerScreen(() -> ItemCollectors.filter_collector_container, container -> WidgetContainerScreen.of(new AdvancedCollectorScreen(container.level, container.getCollectorPosition()), container, false));
        handler.registerCustomBlockEntityRenderer(() -> ItemCollectors.basic_collector_tile, CollectorBlockEntityRenderer::new);
        handler.registerCustomBlockEntityRenderer(() -> ItemCollectors.advanced_collector_tile, CollectorBlockEntityRenderer::new);
    }

    public static void openBasicCollectorScreen(Level level, BlockPos pos){
        ClientUtils.displayScreen(WidgetScreen.of(new BasicCollectorScreen(level, pos)));
    }

    public static boolean onBlockHighlight(WorldRenderContext renderContext, WorldRenderContext.BlockOutlineContext blockOutlineContext){
        Level level = ClientUtils.getWorld();
        BlockEntity entity = level.getBlockEntity(blockOutlineContext.blockPos());
        if(entity instanceof CollectorBlockEntity){
            renderContext.matrixStack().pushPose();
            Vec3 camera = RenderUtils.getCameraPosition();
            renderContext.matrixStack().translate(-camera.x, -camera.y, -camera.z);

            AABB area = ((CollectorBlockEntity)entity).getAffectedArea().inflate(0.05f);

            Random random = new Random(entity.getBlockPos().hashCode());
            float red = random.nextFloat();
            float green = random.nextFloat();
            float blue = random.nextFloat();
            float alpha = 0.3f;

            RenderUtils.renderBox(renderContext.matrixStack(), area, red, green, blue, true);
            RenderUtils.renderBoxSides(renderContext.matrixStack(), area, red, green, blue, alpha, true);

            renderContext.matrixStack().popPose();
        }
        return true;
    }
}
