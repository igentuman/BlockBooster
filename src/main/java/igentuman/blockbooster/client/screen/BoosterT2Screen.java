package igentuman.blockbooster.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.client.screen.element.CheckBox;
import igentuman.blockbooster.container.BoosterT2Container;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BoosterT2Screen extends AbstractContainerScreen<BoosterT2Container> {

    private final ResourceLocation GUI = new ResourceLocation(BlockBooster.MODID, "textures/gui/blockbooster_t2_gui.png");

    private List<CheckBox> checkboxes = new ArrayList<>();


    public BoosterT2Screen(BoosterT2Container container, Inventory inv, Component name) {
        super(container, inv, name);
        imageWidth = 180;
        imageHeight = 152;
    }

    @Override
    public void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY) {
        this.renderBackground(graphics);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
        this.renderTooltip(graphics, pMouseX, pMouseY);
        drawEnergyBar(graphics);
        drawAttachedBlocks(graphics);
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, int x, int y) {
        if(x > getGuiLeft()+4 && x < getGuiLeft()+175 && y > getGuiTop()+139 && y < getGuiTop()+149) {
            Component textComponent = Component.translatable("gui.energy.info", menu.getEnergy(), menu.getMaxEnergy());
            graphics.renderTooltip(Minecraft.getInstance().font, textComponent, x, y);
        }
    }

    protected void init() {
        super.init();
        this.checkboxes.clear();
        this.checkboxes.add(new CheckBox(getGuiLeft()+28, getGuiTop()+15, 13, 13, 181, 0, 13, GUI, (Button btn) -> this.checkboxClicked(btn, 0)));
        this.checkboxes.add(new CheckBox(getGuiLeft()+28, getGuiTop()+35, 13, 13, 181, 0, 13, GUI, (Button btn) -> this.checkboxClicked(btn, 1)));
        this.checkboxes.add(new CheckBox(getGuiLeft()+28, getGuiTop()+55, 13, 13, 181, 0, 13, GUI, (Button btn) -> this.checkboxClicked(btn, 2)));
        this.checkboxes.add(new CheckBox(getGuiLeft()+28, getGuiTop()+75, 13, 13, 181, 0, 13, GUI, (Button btn) -> this.checkboxClicked(btn, 3)));
        this.checkboxes.add(new CheckBox(getGuiLeft()+28, getGuiTop()+95, 13, 13, 181, 0, 13, GUI, (Button btn) -> this.checkboxClicked(btn, 4)));
        this.checkboxes.add(new CheckBox(getGuiLeft()+28, getGuiTop()+115, 13, 13, 181, 0, 13, GUI, (Button btn) -> this.checkboxClicked(btn, 5)));
        for (Button btn: checkboxes) {
            addRenderableWidget(btn);
        }
    }

    public Button.OnPress checkboxClicked(Button btn, int id)
    {
        if(checkboxes == null) return AbstractButton::onPress;
        CheckBox checkBox = checkboxes.get(id);
        byte val = 0;
        if(!checkBox.isChecked()) {
            val = 1;
        }
        menu.checkboxClicked(id, val);
        return checkBox::onPress;
    }

    public void containerTick() {
        super.containerTick();
        byte[] boostFlags = menu.getBoostFlags();
        int i = 0;
        for(byte flag: boostFlags) {
            checkboxes.get(i).setChecked(flag == 1);
            i++;
        }
    }

    protected HashMap<Integer, BlockEntity> getAttachedBlocks()
    {
        return  menu.getAttachedBlocks();
    }

    public void drawEnergyBar(GuiGraphics graphics)
    {
        graphics.blit(GUI, getGuiLeft()+4, getGuiTop()+140, 0, 153, menu.getEnergyScaled(171), 7);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(Minecraft.getInstance().font, I18n.get("gui.block_booster"), imageWidth/2, 4, 0xffffff );

        if(menu.isDisabled()) {
            graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.block_booster.disabled"), 10, 65, 0xffffff);
        }
    }


    private void drawAttachedBlocks(GuiGraphics graphics) {
        int y = 14;
        for(Integer i: menu.getAttachedBlocks().keySet()) {
            graphics.renderItem(
                    new ItemStack(menu.getAttachedBlocks().get(i).getBlockState().getBlock().asItem()), getGuiLeft()+9, getGuiTop()+i*20+y);
        }
    }
}
