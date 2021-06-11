package com.supermartijn642.itemcollectors;

import com.supermartijn642.core.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> e){
        ClientRegistry.bindTileEntitySpecialRenderer(CollectorTile.class, new CollectorTileRenderer());
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e){
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ItemCollectors.basic_collector), 0, new ModelResourceLocation(ItemCollectors.basic_collector.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ItemCollectors.advanced_collector), 0, new ModelResourceLocation(ItemCollectors.advanced_collector.getRegistryName(), "inventory"));
    }

    @Mod.EventBusSubscriber(Side.CLIENT)
    public static class Events {

        @SubscribeEvent
        public static void onBlockHighlight(DrawBlockHighlightEvent e){
            if(e.getTarget().typeOfHit != RayTraceResult.Type.BLOCK || e.getTarget().getBlockPos() == null)
                return;

            World world = ClientUtils.getWorld();
            TileEntity tile = world.getTileEntity(e.getTarget().getBlockPos());
            if(tile instanceof CollectorTile)
                drawShape(((CollectorTile)tile).getAffectedArea(), 245 / 255f, 212 / 255f, 66 / 255f, 1);
        }
    }

    public static void drawShape(AxisAlignedBB box, float red, float green, float blue, float alpha){
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        RenderManager manager = ClientUtils.getMinecraft().getRenderManager();
        GlStateManager.translate(-manager.viewerPosX, -manager.viewerPosY, -manager.viewerPosZ);

        RenderGlobal.drawSelectionBoundingBox(box, red, green, blue, alpha);

        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
    }

}
