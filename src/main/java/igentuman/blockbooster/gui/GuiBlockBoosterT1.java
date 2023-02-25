package igentuman.blockbooster.gui;

import igentuman.blockbooster.ModInfo;
import igentuman.blockbooster.container.ContainerBlockBoosterT1;
import igentuman.blockbooster.gui.element.Checkbox;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuiBlockBoosterT1 extends GuiContainer {
    private static final ResourceLocation background = new ResourceLocation(
            ModInfo.MODID, "textures/gui/container/booster_t1.png"
    );
    Minecraft MC = Minecraft.getMinecraft();
    private List<Checkbox> checkboxes = new ArrayList<>();

    private final ContainerBlockBoosterT1 container;

    public GuiBlockBoosterT1(ContainerBlockBoosterT1 inventorySlotsIn) {
        super(inventorySlotsIn);
        this.container = inventorySlotsIn;
        xSize=180;
        ySize=100;
    }

    public void initGui()
    {
        super.initGui();
        checkboxes = new ArrayList<>();
        checkboxes.add(new Checkbox(0,guiLeft+28,guiTop+15,13, 13, 181, 0, 13, background));
        checkboxes.add(new Checkbox(1,guiLeft+28,guiTop+35,13, 13, 181, 0, 13, background));
        for (Checkbox btn: checkboxes) {
            buttonList.add(btn);
        }
    }

    protected void renderItem(int x, int y, ItemStack stack) {

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

    protected void renderModelAndEffect(ItemStack itemStack, IBakedModel model) {
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

    protected void renderEffect(IBakedModel model) {
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

    protected void renderModel(IBakedModel model, int color, ItemStack itemStack) {
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

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(background);

        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        drawEnergyBar();
        drawAttachedBlocks();

    }

    protected HashMap<Integer, TileEntity> getAttachedBlocks()
    {
        return  container.getAttachedBlocks();
    }

    public void drawEnergyBar()
    {
        drawTexturedModalRect(4, 67, 0, 153, container.getEnergyScaled(171), 7);
    }

    public ItemStack getItemStackFromTile(TileEntity te)
    {
        IBlockState actualState = te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos());
        Block block = te.getBlockType().getBlockState().getBlock();

        int id = Block.getIdFromBlock(block);
        int meta = block.getMetaFromState(actualState);
        return new ItemStack(block, 1, meta);
    }

    private void drawAttachedBlocks() {
        int y = 13;
        if(getAttachedBlocks().size() < 1) return;
        for(Integer i: getAttachedBlocks().keySet()) {
            renderItem(guiLeft+9, guiTop+i+20+y,
                    getItemStackFromTile(getAttachedBlocks().get(i))
            );
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)this.guiLeft, (float)this.guiTop, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();

        GlStateManager.popMatrix();
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(I18n.format("booster.container.title"), 8, 6, 4210752);

    }
}
