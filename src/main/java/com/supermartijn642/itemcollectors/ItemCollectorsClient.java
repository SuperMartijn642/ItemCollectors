package com.supermartijn642.itemcollectors;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.block.BlockShape;
import com.supermartijn642.core.gui.WidgetContainerScreen;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.registry.ClientRegistrationHandler;
import com.supermartijn642.core.render.RenderUtils;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorScreen;
import com.supermartijn642.itemcollectors.screen.BasicCollectorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class ItemCollectorsClient implements ClientModInitializer {

    private static final RenderStateDataKey<AreaHighlightState> HIGHLIGHT_DATA = RenderStateDataKey.create(() -> "itemcollectors:demagnetization_coil_area_highlight");
    private static final PoseStack POSE_STACK = new PoseStack();

    @Override
    public void onInitializeClient(){
        LevelExtractionEvents.AFTER_BLOCK_OUTLINE_EXTRACTION.register(ItemCollectorsClient::onBlockHighlightExtract);
        LevelRenderEvents.BEFORE_BLOCK_OUTLINE.register(ItemCollectorsClient::onBlockHighlightDraw);

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

    private static void onBlockHighlightExtract(LevelExtractionContext context, HitResult result){
        AreaHighlightState state = context.levelState().getData(HIGHLIGHT_DATA);
        if(state == null){
            state = new AreaHighlightState();
            context.levelState().setData(HIGHLIGHT_DATA, state);
        }
        state.shouldRender = false;

        if(result instanceof BlockHitResult){
            BlockPos pos = ((BlockHitResult)result).getBlockPos();
            BlockEntity entity = context.level().getBlockEntity(pos);
            if(entity instanceof CollectorBlockEntity){
                state.shouldRender = true;
                state.pos = pos;
                state.area = ((CollectorBlockEntity)entity).getAffectedArea();
            }
        }
    }

    private static boolean onBlockHighlightDraw(LevelRenderContext context, BlockOutlineRenderState outlineRenderState){
        AreaHighlightState state = context.levelState().getData(HIGHLIGHT_DATA);
        if(state == null || !state.shouldRender)
            return true;

        POSE_STACK.pushPose();
        Vec3 playerPos = context.levelState().cameraRenderState.pos;
        POSE_STACK.translate(-playerPos.x, -playerPos.y, -playerPos.z);

        Random random = new Random(state.pos.hashCode());
        float red = random.nextFloat();
        float green = random.nextFloat();
        float blue = random.nextFloat();
        float alpha = 0.3f;

        SubmitNodeCollector output = context.submitNodeCollector();
        RenderUtils.submitShape(output, POSE_STACK, BlockShape.create(state.area), red, green, blue, alpha, true);
        RenderUtils.submitShapeSides(output, POSE_STACK, BlockShape.create(state.area), red, green, blue, alpha, true);

        POSE_STACK.popPose();
        return true;
    }

    private static class AreaHighlightState {
        boolean shouldRender;
        BlockPos pos;
        AABB area;
    }
}
