package igentuman.blockbooster.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeModContainer;

import java.awt.*;

public class ClientUtil {

    public static Minecraft MC = Minecraft.getMinecraft();

    public static void renderItem(int x, int y, ItemStack stack) {
        MC.getRenderItem().zLevel += 50F;
        GlStateManager.pushMatrix();
        MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        MC.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1F, 1F, 1F, 1F);
        IBakedModel model = MC.getRenderItem().getItemModelWithOverrides(stack, null, null);
        GlStateManager.translate(x, y, 100F + MC.getRenderItem().zLevel);
        GlStateManager.translate(8F, 8F, 0F);
        GlStateManager.scale(1F, -1F, 1F);
        GlStateManager.scale(16, 16F, 16);
        GlStateManager.disableLighting();
        model = ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.GUI, false);
        renderModelAndEffect(stack, model);
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        MC.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        MC.getRenderItem().zLevel -= 50F;
    }

    public static void renderModelAndEffect(ItemStack itemStack, IBakedModel model) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);

        if (model.isBuiltInRenderer()) {
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.enableRescaleNormal();
            itemStack.getItem().getTileEntityItemStackRenderer().renderByItem(itemStack);
        }
        else {
            renderModel(model, new Color(1F, 1F, 1F, 1F).getRGB(), itemStack);
            if (itemStack.hasEffect()) {
                renderEffect(model);
            }
        }

        GlStateManager.popMatrix();
    }

    public static void renderEffect(IBakedModel model) {
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        MC.getTextureManager().bindTexture(new ResourceLocation("textures/misc/enchanted_item_glint.png"));
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(8F, 8F, 8F);
        float f = (Minecraft.getSystemTime() % 3000L) / 24000F;
        GlStateManager.translate(f, 0F, 0F);
        GlStateManager.rotate(-50F, 0F, 0F, 1F);
        renderModel(model, -8372020, ItemStack.EMPTY);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8F, 8F, 8F);
        float f1 = (Minecraft.getSystemTime() % 4873L) / 38984F;
        GlStateManager.translate(-f1, 0F, 0F);
        GlStateManager.rotate(10F, 0F, 0F, 1F);
        renderModel(model, -8372020, ItemStack.EMPTY);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }

    public static void renderModel(IBakedModel model, int color, ItemStack itemStack) {
        if (ForgeModContainer.allowEmissiveItems) {
            ForgeHooksClient.renderLitItem(MC.getRenderItem(), model, color, itemStack);
            return;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.ITEM);

        for (EnumFacing facing : EnumFacing.VALUES) {
            MC.getRenderItem().renderQuads(bufferbuilder, model.getQuads(null, facing, 0L), color, itemStack);
        }

        MC.getRenderItem().renderQuads(bufferbuilder, model.getQuads(null, null, 0L), color, itemStack);
        tessellator.draw();
    }
}
