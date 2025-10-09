package igentuman.blockbooster.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.client.screen.element.CheckBox;
import igentuman.blockbooster.container.BoosterManaContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class BoosterManaScreen extends AbstractContainerScreen<BoosterManaContainer> {

    private final ResourceLocation GUI = new ResourceLocation(BlockBooster.MODID, "textures/gui/blockbooster_mana_gui.png");
    private List<CheckBox> checkboxes = new ArrayList<>();


    public BoosterManaScreen(BoosterManaContainer container, Inventory inv, Component name) {
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
        drawManaBar(graphics);
        drawAttachedBlocks(graphics);
        drawTPSIndicator(graphics);
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

    public void drawManaBar(GuiGraphics graphics)
    {
        graphics.blit(GUI, getGuiLeft()+4, getGuiTop()+140, 0, 153, menu.getManaScaled(171), 7);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(Minecraft.getInstance().font, I18n.get("gui.block_booster"), imageWidth/2, 4, 0xffffff );

        if(menu.isDisabled()) {
            graphics.drawString(Minecraft.getInstance().font,Component.translatable("gui.block_booster.disabled"), 10, 65, 0xffffff);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, int x, int y) {
        if(x > getGuiLeft()+4 && x < getGuiLeft()+175 && y > getGuiTop()+139 && y < getGuiTop()+149) {
            Component textComponent = Component.translatable("gui.mana.info", menu.getMana(), menu.getMaxMana());
            graphics.renderTooltip(Minecraft.getInstance().font, textComponent, x, y);
            return;
        }
        
        // TPS indicator tooltip
        int tpsX = getGuiLeft() + 165;
        int tpsY = getGuiTop() + 4;
        if(x >= tpsX && x < tpsX + 8 && y >= tpsY && y < tpsY + 8) {
            List<Component> tooltip = new ArrayList<>();
            if(menu.isLagging()) {
                tooltip.add(Component.translatable("gui.tps.lagging"));
                tooltip.add(Component.translatable("gui.tps.current", String.format("%.1f", menu.getCurrentTPS())));
                tooltip.add(Component.translatable("gui.tps.boosting_paused"));
            } else {
                tooltip.add(Component.translatable("gui.tps.ok"));
                tooltip.add(Component.translatable("gui.tps.current", String.format("%.1f", menu.getCurrentTPS())));
            }
            graphics.renderTooltip(Minecraft.getInstance().font, tooltip, Optional.empty(), x, y);
            return;
        }
        
        // Check for attached block tooltips
        HashMap<Integer, BlockEntity> attachedBlocks = menu.getAttachedBlocks();
        int blockIconX = getGuiLeft() + 9;
        int blockIconSize = 16;
        int blockSpacing = 20;
        int startY = getGuiTop() + 14;
        
        for(Integer i: attachedBlocks.keySet()) {
            BlockEntity be = attachedBlocks.get(i);
            if(be != null && !be.isRemoved()) {
                int blockY = startY + i * blockSpacing;
                if(x >= blockIconX && x < blockIconX + blockIconSize && 
                   y >= blockY && y < blockY + blockIconSize) {
                    ItemStack blockStack = new ItemStack(be.getBlockState().getBlock().asItem());
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(Component.translatable(blockStack.getDescriptionId()));
                    tooltip.add(Component.literal(be.getBlockPos().toShortString()));
                    graphics.renderTooltip(Minecraft.getInstance().font, tooltip, Optional.empty(), x, y);
                    return;
                }
            }
        }
    }

    private void drawAttachedBlocks(GuiGraphics graphics) {
        int y = 14;
        for(Integer i: menu.getAttachedBlocks().keySet()) {
            graphics.renderItem(
                    new ItemStack(menu.getAttachedBlocks().get(i).getBlockState().getBlock().asItem()), getGuiLeft()+9, getGuiTop()+i*20+y);
        }
    }

    private void drawTPSIndicator(GuiGraphics graphics) {
        int x = getGuiLeft() + 165;
        int y = getGuiTop() + 4;
        int size = 8;
        
        // Draw indicator background (small square)
        int color = menu.isLagging() ? 0xFFFF0000 : 0xFF00FF00; // Red if lagging, green if OK
        graphics.fill(x, y, x + size, y + size, color);
        
        // Draw border
        graphics.fill(x, y, x + size, y + 1, 0xFF000000); // Top
        graphics.fill(x, y + size - 1, x + size, y + size, 0xFF000000); // Bottom
        graphics.fill(x, y, x + 1, y + size, 0xFF000000); // Left
        graphics.fill(x + size - 1, y, x + size, y + size, 0xFF000000); // Right
    }
}
