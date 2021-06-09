package com.supermartijn642.itemcollectors;

import com.mojang.blaze3d.platform.GlStateManager;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorContainer;
import com.supermartijn642.itemcollectors.screen.AdvancedCollectorScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.opengl.GL11;

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
                Vec3d projectedView = e.getInfo().getProjectedView();

                GlStateManager.disableTexture();

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder builder = tessellator.getBuffer();
                builder.setTranslation(-projectedView.x, -projectedView.y, -projectedView.z);

                builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                drawShape(builder, ((CollectorTile)tile).getAffectedArea(), 245 / 255f, 212 / 255f, 66 / 255f, 1);

                builder.setTranslation(0, 0, 0);
                tessellator.draw();

                GlStateManager.enableTexture();
            }
        }
    }

    public static void drawShape(BufferBuilder builder, AxisAlignedBB box, float red, float green, float blue, float alpha){
        VoxelShapes.create(box).forEachEdge((x1, y1, z1, x2, y2, z2) -> {
            builder.pos((float)x1, (float)y1, (float)z1).color(red, green, blue, alpha).endVertex();
            builder.pos((float)x2, (float)y2, (float)z2).color(red, green, blue, alpha).endVertex();
        });
    }

}
