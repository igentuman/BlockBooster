package igentuman.blockbooster.client.screen.element;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CheckBox extends Button {

    private ResourceLocation resourceLocation;
    private int xTexStart;
    private int yTexStart;
    private int yDiffTex;
    private int textureWidth;
    private int textureHeight;

    public boolean isChecked() {
        return isChecked;
    }

    private boolean isChecked = false;

    public CheckBox(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int yDiffTex, ResourceLocation pResourceLocation, Button.OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, Component.translatable("gui.checkbox.boost"), pOnPress, DEFAULT_NARRATION);
        resourceLocation = pResourceLocation;
        xTexStart = pXTexStart;
        yTexStart = pYTexStart;
        this.yDiffTex = yDiffTex;
        this.textureWidth = 256;
        this.textureHeight = 256;
    }

    public void toggleChecked()
    {
        isChecked = !isChecked;
        if(isChecked) {
            setXStart(194);
        } else {
            setXStart(181);
        }
    }

    public void setChecked(boolean val)
    {
        isChecked = val;
        if(isChecked) {
            setXStart(194);
        } else {
            setXStart(181);
        }
    }

    public void setXStart(int val)
    {
        xTexStart = val;
    }

    public void onPress(Button button) {
        toggleChecked();
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.resourceLocation);
        int i = this.yTexStart;
        if (!this.isActive()) {
            i += this.yDiffTex * 2;
        } else if (this.isHoveredOrFocused()) {
            i += this.yDiffTex;
        }

        RenderSystem.enableDepthTest();
        guiGraphics.blit(resourceLocation, getX(), getY(), xTexStart, i, width, height, textureWidth, textureHeight);
        if (this.isHovered) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, getMessage(), pMouseX, pMouseY);
        }
        guiGraphics.drawString(Minecraft.getInstance().font, I18n.get("gui.checkbox.boost"), getX()+15, getY()+3, 0xffffff);

    }

}
