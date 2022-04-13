package igentuman.blockbooster.client;

import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.block.BoosterContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BoosterScreen extends AbstractContainerScreen<BoosterContainer> {

    private final ResourceLocation GUI = new ResourceLocation(BlockBooster.MODID, "textures/gui/blockbooster_gui.png");

    public BoosterScreen(BoosterContainer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        drawString(matrixStack, Minecraft.getInstance().font, "Energy: " + menu.getEnergy(), 10, 10, 0x000000);
        drawString(matrixStack, Minecraft.getInstance().font, "On Top: " + menu.getTopBlock(), 10, 20, 0x000000);
        drawString(matrixStack, Minecraft.getInstance().font, "On Bottom: " + menu.getBottomBlock(), 10, 30, 0x000000);
       // drawString(matrixStack, Minecraft.getInstance().font, "On Left: " + menu.getLeftBlock(), 10, 40, 0xffffff);
        //drawString(matrixStack, Minecraft.getInstance().font, "On Right: " + menu.getRightBlock(), 10, 50, 0xffffff);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }
}
