package igentuman.blockbooster.client.screen;

import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.client.screen.element.CheckBox;
import igentuman.blockbooster.container.BoosterT2Container;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class BoosterT2Screen extends AbstractContainerScreen<BoosterT2Container> {

    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(BlockBooster.MODID, "textures/gui/blockbooster_t2_gui.png");
    private final HashMap<Long, CheckBox> checkboxes = new HashMap<>();

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
        drawTPSIndicator(graphics);
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, int x, int y) {
        if(x > getGuiLeft()+4 && x < getGuiLeft()+175 && y > getGuiTop()+139 && y < getGuiTop()+149) {
            Component textComponent = Component.translatable("gui.energy.info", menu.getEnergy(), menu.getMaxEnergy());
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
        HashMap<Long, BlockEntity> attachedBlocks = menu.getAttachedBlocks();
        int blockIconX = getGuiLeft() + 9;
        int blockIconSize = 16;
        int blockSpacing = 20;
        int startY = getGuiTop() + 14;
        
        int index = 0;
        for(Long posKey: attachedBlocks.keySet()) {
            BlockEntity be = attachedBlocks.get(posKey);
            if(be != null && !be.isRemoved()) {
                int blockY = startY + index * blockSpacing;
                if(x >= blockIconX && x < blockIconX + blockIconSize && 
                   y >= blockY && y < blockY + blockIconSize) {
                    ItemStack blockStack = new ItemStack(be.getBlockState().getBlock().asItem());
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(Component.translatable(blockStack.getDescriptionId()));
                    tooltip.add(Component.literal(be.getBlockPos().toShortString()));
                    graphics.renderTooltip(Minecraft.getInstance().font, tooltip, Optional.empty(), x, y);
                    return;
                }
                index++;
            }
        }
    }

    protected void init() {
        super.init();
        this.checkboxes.clear();
        int index = 0;
        for(long id: menu.getBoostFlags().keySet()) {
            checkboxes.put(id, new CheckBox(getGuiLeft()+28, getGuiTop()+15 + index*20, 13, 13, 181, 0, 13, GUI, (Button btn) -> this.checkboxClicked(btn, id)));
            checkboxes.get(id).setChecked(menu.getBoostFlags().get(id));
            index++;
        }
        for (Button btn: checkboxes.values()) {
            addRenderableWidget(btn);
        }
    }

    public Button.OnPress checkboxClicked(Button btn, long id)
    {
        if(checkboxes == null) return AbstractButton::onPress;
        CheckBox checkBox = checkboxes.get(id);
        boolean val = false;
        if(!checkBox.isChecked()) {
            val = true;
        }
        menu.checkboxClicked(id, val);
        return checkBox::onPress;
    }

    public void containerTick() {
        super.containerTick();
        for(long id: menu.getBoostFlags().keySet()) {
            if(!checkboxes.containsKey(id)) continue;
            checkboxes.get(id).setChecked(menu.getBoostFlags().get(id));
        }
    }

    protected HashMap<Long, BlockEntity> getAttachedBlocks()
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
        int index = 0;
        for(Long posKey: menu.getAttachedBlocks().keySet()) {
            graphics.renderItem(
                    new ItemStack(menu.getAttachedBlocks().get(posKey).getBlockState().getBlock().asItem()), getGuiLeft()+9, getGuiTop()+index*20+y);
            index++;
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
