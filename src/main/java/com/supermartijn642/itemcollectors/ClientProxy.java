package com.supermartijn642.itemcollectors;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorContainer;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShapes;
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
            if(tile instanceof CollectorTile){
                MatrixStack matrixStack = e.getMatrix();
                matrixStack.push();

                Vec3d playerPos = Minecraft.getInstance().player.getEyePosition(e.getPartialTicks());
                matrixStack.translate(-playerPos.x, -playerPos.y, -playerPos.z);
                IVertexBuilder builder = e.getBuffers().getBuffer(RenderType.getLines());

                drawShape(matrixStack, builder, ((CollectorTile)tile).getAffectedArea(), 245 / 255f, 212 / 255f, 66 / 255f, 1);

                matrixStack.pop();
            }
        }
    }

    public static void drawShape(MatrixStack matrixStackIn, IVertexBuilder bufferIn, AxisAlignedBB box, float red, float green, float blue, float alpha){
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        VoxelShapes.create(box).forEachEdge((x1, y1, z1, x2, y2, z2) -> {
            bufferIn.pos(matrix4f, (float)x1, (float)y1, (float)z1).color(red, green, blue, alpha).endVertex();
            bufferIn.pos(matrix4f, (float)x2, (float)y2, (float)z2).color(red, green, blue, alpha).endVertex();
        });
    }

}
