package com.supermartijn642.itemcollectors;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.gui.WidgetContainerScreen;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.registry.ClientRegistrationHandler;
import com.supermartijn642.core.render.RenderUtils;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorScreen;
import com.supermartijn642.itemcollectors.screen.BasicCollectorScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Random;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class ItemCollectorsClient {

    private static final ContextKey<AreaHighlightState> HIGHLIGHT_DATA = new ContextKey<>(ResourceLocation.fromNamespaceAndPath("itemcollectors", "demagnetization_coil_area_highlight"));
    private static final PoseStack POSE_STACK = new PoseStack();

    public static void register(){
        NeoForge.EVENT_BUS.addListener(ItemCollectorsClient::onExtractBlockOutline);

        ClientRegistrationHandler handler = ClientRegistrationHandler.get("itemcollectors");
        handler.registerContainerScreen(() -> ItemCollectors.filter_collector_container, container -> WidgetContainerScreen.of(new AdvancedCollectorScreen(container.level, container.getCollectorPosition()), container, false));
        handler.registerCustomBlockEntityRenderer(() -> ItemCollectors.basic_collector_tile, CollectorBlockEntityRenderer::new);
        handler.registerCustomBlockEntityRenderer(() -> ItemCollectors.advanced_collector_tile, CollectorBlockEntityRenderer::new);
    }

    public static void openBasicCollectorScreen(Level level, BlockPos pos){
        ClientUtils.displayScreen(WidgetScreen.of(new BasicCollectorScreen(level, pos)));
    }

    private static void onExtractBlockOutline(ExtractBlockOutlineRenderStateEvent event){
        AreaHighlightState state = event.getLevelRenderState().getRenderData(HIGHLIGHT_DATA);
        if(state == null){
            state = new AreaHighlightState();
            event.getLevelRenderState().setRenderData(HIGHLIGHT_DATA, state);
        }
        state.shouldRender = false;

        BlockPos pos = event.getHitResult().getBlockPos();
        BlockEntity entity = event.getLevel().getBlockEntity(pos);
        if(entity instanceof CollectorBlockEntity){
            state.shouldRender = true;
            state.pos = pos;
            state.area = ((CollectorBlockEntity)entity).getAffectedArea();
            event.addCustomRenderer(ItemCollectorsClient::onRenderBlockOutline);
        }
    }

    private static boolean onRenderBlockOutline(BlockOutlineRenderState outlineRenderState, MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, boolean translucentPass, LevelRenderState levelRenderState){
        AreaHighlightState state = levelRenderState.getRenderData(HIGHLIGHT_DATA);
        if(state == null || !state.shouldRender)
            return false;

        POSE_STACK.pushPose();
        Vec3 playerPos = levelRenderState.cameraRenderState.pos;
        POSE_STACK.translate(-playerPos.x, -playerPos.y, -playerPos.z);

        Random random = new Random(state.pos.hashCode());
        float red = random.nextFloat();
        float green = random.nextFloat();
        float blue = random.nextFloat();
        float alpha = 0.3f;

        RenderUtils.renderBox(POSE_STACK, state.area, red, green, blue, alpha, true);
        RenderUtils.renderBoxSides(POSE_STACK, state.area, red, green, blue, alpha, true);

        POSE_STACK.popPose();
        return false;
    }

    private static class AreaHighlightState {
        boolean shouldRender;
        BlockPos pos;
        AABB area;
    }
}
