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
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.client.event.RenderHighlightEvent;

import java.util.Random;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class ItemCollectorsClient {

    private static final PoseStack POSE_STACK = new PoseStack();

    public static void register(){
        RenderHighlightEvent.Block.BUS.addListener(ItemCollectorsClient::onBlockHighlightExtract);

        ClientRegistrationHandler handler = ClientRegistrationHandler.get("itemcollectors");
        handler.registerContainerScreen(() -> ItemCollectors.filter_collector_container, container -> WidgetContainerScreen.of(new AdvancedCollectorScreen(container.level, container.getCollectorPosition()), container, false));
        handler.registerCustomBlockEntityRenderer(() -> ItemCollectors.basic_collector_tile, CollectorBlockEntityRenderer::new);
        handler.registerCustomBlockEntityRenderer(() -> ItemCollectors.advanced_collector_tile, CollectorBlockEntityRenderer::new);
    }

    public static void openBasicCollectorScreen(Level level, BlockPos pos){
        ClientUtils.displayScreen(WidgetScreen.of(new BasicCollectorScreen(level, pos)));
    }

    private static void onBlockHighlightExtract(RenderHighlightEvent.Block event){
        BlockPos pos = event.getTarget().getBlockPos();
        Level level = ClientUtils.getWorld();
        BlockEntity entity = level.getBlockEntity(pos);
        if(entity instanceof CollectorBlockEntity){
            AreaHighlightState state = new AreaHighlightState();
            state.shouldRender = true;
            state.pos = pos;
            state.area = ((CollectorBlockEntity)entity).getAffectedArea();
            BlockState blockState = level.getBlockState(pos);
            //noinspection deprecation
            BlockOutlineRenderState outlineRenderState = new BlockOutlineRenderState(
                pos,
                ClientUtils.getMinecraft().getModelManager().getBlockStateModelSet().get(blockState).hasMaterialFlag(BakedQuad.FLAG_TRANSLUCENT),
                ClientUtils.getMinecraft().options.highContrastBlockOutline().get(),
                blockState.getShape(level, pos, CollisionContext.of(event.getCamera().entity()))
            );
            event.setCustomRenderer((source, stack, levelRenderState) -> onRenderBlockOutline(outlineRenderState, source, stack, levelRenderState, state));
        }
    }

    private static boolean onRenderBlockOutline(BlockOutlineRenderState outlineRenderState, SubmitNodeCollector output, PoseStack poseStack, LevelRenderState levelRenderState, AreaHighlightState state){
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

        RenderUtils.submitShape(output, POSE_STACK, BlockShape.create(state.area), red, green, blue, alpha, true);
        RenderUtils.submitShapeSides(output, POSE_STACK, BlockShape.create(state.area), red, green, blue, alpha, true);

        POSE_STACK.popPose();

        // Render original outline
        BlockOutlineRenderState temp = levelRenderState.blockOutlineRenderState;
        levelRenderState.blockOutlineRenderState = outlineRenderState;
        ClientUtils.getMinecraft().levelRenderer.submitBlockOutline(poseStack, output, levelRenderState);
        levelRenderState.blockOutlineRenderState = temp;
        return false;
    }

    private static class AreaHighlightState {
        boolean shouldRender;
        BlockPos pos;
        AABB area;
    }
}
