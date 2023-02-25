package igentuman.blockbooster.gui.element;

import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.util.ResourceLocation;

public class Checkbox extends GuiButtonImage {

    private boolean checked = false;

    public Checkbox(int buttonId, int xIn, int yIn, int widthIn, int heightIn, int textureOffestX, int textureOffestY, int p_i47392_8_, ResourceLocation resource) {
        super(buttonId, xIn, yIn, widthIn, heightIn, textureOffestX, textureOffestY, p_i47392_8_, resource);
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
}
