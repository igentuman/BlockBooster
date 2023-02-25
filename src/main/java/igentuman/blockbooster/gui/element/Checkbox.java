package igentuman.blockbooster.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class Checkbox extends GuiButtonImage {

    private ResourceLocation resourceLocation;
    private int xTexStart;
    private int yTexStart;
    private int yDiffText;
    private boolean checked = false;

    public Checkbox(int buttonId, int xIn, int yIn, int widthIn, int heightIn, int textureOffestX, int textureOffestY, int p_i47392_8_, ResourceLocation resource) {
        super(buttonId, xIn, yIn, widthIn, heightIn, textureOffestX, textureOffestY, p_i47392_8_, resource);
        this.xTexStart = textureOffestX;
        this.yTexStart = textureOffestY;
        this.yDiffText = p_i47392_8_;
        this.resourceLocation = resource;
    }

    public boolean isChecked() {
        return checked;
    }

    public void toggleCheck() {
        checked = !checked;
    }

    public void setChecked(boolean val)
    {
        checked = val;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            mc.getTextureManager().bindTexture(this.resourceLocation);
            GlStateManager.disableDepth();
            int i = this.xTexStart;
            int j = this.yTexStart;
            if (this.hovered) {
                j += this.yDiffText;
            }
            if(checked) {
                i+=13;
            }

            this.drawTexturedModalRect(this.x, this.y, i, j, this.width, this.height);
            mc.fontRenderer.drawString(I18n.format("gui.booster.checkbox"),x+15,y+3, 4210752);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.enableDepth();
        }
    }
}
