package igentuman.blockbooster.gui;

import igentuman.blockbooster.ModInfo;
import igentuman.blockbooster.container.ContainerBlockBooster;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import static igentuman.blockbooster.ModConfig.DEBUG;
import static igentuman.blockbooster.ModConfig.boosterConfig;

public class GuiBlockBooster extends GuiContainer {
    private static final ResourceLocation background = new ResourceLocation(
            ModInfo.MODID, "textures/gui/container/booster.png"
    );

    private final ContainerBlockBooster container;

    public GuiBlockBooster(ContainerBlockBooster inventorySlotsIn) {
        super(inventorySlotsIn);
        this.container = inventorySlotsIn;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(background);

        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);


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
        this.fontRenderer.drawString(I18n.format("booster.container.energy") + " "+ container.getEnergyStored() + " RF", 8, 26, 4210752);
        this.fontRenderer.drawString(I18n.format("booster.container.top_te") + " "+ container.getTopTe(), 8, 36, 4210752);
        this.fontRenderer.drawString(I18n.format("booster.container.bottom_te") + " "+ container.getBottomTe(), 8, 46, 4210752);
        if(container.isWorking()) {
            this.fontRenderer.drawString(I18n.format("booster.container.is_working"), 8, 56, 4210752);
        } else {
            this.fontRenderer.drawString(I18n.format("booster.container.is_not_working"), 8, 56, 4210752);
        }

        if(DEBUG) {
            this.fontRenderer.drawString("T: " + container.debugTopTe(), 8, 66, 4210752);
            this.fontRenderer.drawString("B: "+ container.debugBottomTe(), 8, 76, 4210752);

        }
    }
}
