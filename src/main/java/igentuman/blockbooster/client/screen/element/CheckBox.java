package igentuman.blockbooster.client.screen.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class CheckBox extends ImageButton {

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

    public CheckBox(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int yDiffTex, ResourceLocation pResourceLocation, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, yDiffTex, pResourceLocation, pOnPress);
        resourceLocation = pResourceLocation;
        yTexStart = pYTexStart;
        this.yDiffTex = yDiffTex;
        textureWidth = pWidth;
        textureHeight = pHeight;
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


    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.resourceLocation);
        int i = this.yTexStart;
        if (!this.isActive()) {
            i += this.yDiffTex * 2;
        } else if (this.isHoveredOrFocused()) {
            i += this.yDiffTex;
        }

        RenderSystem.enableDepthTest();
        blit(pPoseStack, this.x, this.y, (float)this.xTexStart, (float)i, this.width, this.height, this.textureWidth, this.textureHeight);
        if (this.isHovered) {
            this.renderToolTip(pPoseStack, pMouseX, pMouseY);
        }

    }
}
