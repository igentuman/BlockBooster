package igentuman.blockbooster.client.screen;

import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.client.screen.element.CheckBox;
import igentuman.blockbooster.container.BoosterT3Container;
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

public class BoosterT3Screen extends AbstractContainerScreen<BoosterT3Container> {

    private final ResourceLocation GUI = new ResourceLocation(BlockBooster.MODID, "textures/gui/blockbooster_t2_gui.png");

    private List<CheckBox> checkboxes = new ArrayList<>();
    private int scrollOffset = 0;
    private int maxVisibleItems = 6;
    private int totalItems = 0;

    public BoosterT3Screen(BoosterT3Container container, Inventory inv, Component name) {
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
        drawScrollBar(graphics);
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
        HashMap<Integer, BlockEntity> attachedBlocks = menu.getAttachedBlocks();
        HashMap<Integer, Long> boostTimes = menu.getBoostTimes();
        List<Integer> sortedKeys = new ArrayList<>(attachedBlocks.keySet());
        sortedKeys.sort(Integer::compareTo);
        
        int blockIconX = getGuiLeft() + 9;
        int blockIconSize = 16;
        int blockSpacing = 20;
        int startY = getGuiTop() + 14;
        
        int displayIndex = 0;
        for(int i = scrollOffset; i < Math.min(scrollOffset + maxVisibleItems, sortedKeys.size()); i++) {
            Integer key = sortedKeys.get(i);
            BlockEntity be = attachedBlocks.get(key);
            if(be != null && !be.isRemoved()) {
                int blockY = startY + displayIndex * blockSpacing;
                if(x >= blockIconX && x < blockIconX + blockIconSize && 
                   y >= blockY && y < blockY + blockIconSize) {
                    ItemStack blockStack = new ItemStack(be.getBlockState().getBlock().asItem());
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(Component.translatable(blockStack.getDescriptionId()));
                    tooltip.add(Component.literal(be.getBlockPos().toShortString()));
                    
                    // Add boost time information
                    Long boostTime = boostTimes.get(key);
                    if(boostTime != null && boostTime > 0) {
                        double timeMs = boostTime / 1_000_000.0;
                        tooltip.add(Component.translatable("gui.boost.time", String.format("%.3f", timeMs)));
                        
                        if(menu.isSlowBlock(key)) {
                            tooltip.add(Component.translatable("gui.boost.slow_block"));
                        }
                    }
                    
                    graphics.renderTooltip(Minecraft.getInstance().font, tooltip, Optional.empty(), x, y);
                    return;
                }
            }
            displayIndex++;
        }
    }

    protected void init() {
        super.init();
        updateCheckboxes();
    }

    private void updateCheckboxes() {
        // Clear existing checkboxes
        for(CheckBox checkbox : checkboxes) {
            removeWidget(checkbox);
        }
        this.checkboxes.clear();

        totalItems = menu.getAttachedBlocks().size();
        
        // Create checkboxes for visible items
        for(int i = 0; i < Math.min(maxVisibleItems, totalItems - scrollOffset); i++) {
            int actualIndex = i + scrollOffset;
            CheckBox checkbox = new CheckBox(
                getGuiLeft()+28, 
                getGuiTop()+15 + i*20, 
                13, 13, 181, 0, 13, GUI, 
                (Button btn) -> this.checkboxClicked(btn, actualIndex)
            );
            this.checkboxes.add(checkbox);
            addRenderableWidget(checkbox);
        }
    }

    public Button.OnPress checkboxClicked(Button btn, int id)
    {
        if(checkboxes == null) return AbstractButton::onPress;
        int localIndex = id - scrollOffset;
        if(localIndex < 0 || localIndex >= checkboxes.size()) return AbstractButton::onPress;
        
        CheckBox checkBox = checkboxes.get(localIndex);
        byte val = 0;
        if(!checkBox.isChecked()) {
            val = 1;
        }
        menu.checkboxClicked(id, val);
        return checkBox::onPress;
    }

    public void containerTick() {
        super.containerTick();
        
        // Update total items count
        int newTotalItems = menu.getAttachedBlocks().size();
        if(newTotalItems != totalItems) {
            totalItems = newTotalItems;
            // Adjust scroll offset if needed
            if(scrollOffset > Math.max(0, totalItems - maxVisibleItems)) {
                scrollOffset = Math.max(0, totalItems - maxVisibleItems);
            }
            updateCheckboxes();
        }
        
        // Update checkbox states
        HashMap<Integer, Boolean> boostFlags = menu.getBoostFlags();
        for(int i = 0; i < checkboxes.size(); i++) {
            int actualIndex = i + scrollOffset;
            checkboxes.get(i).setChecked(boostFlags.getOrDefault(actualIndex, false));
        }
    }

    protected HashMap<Integer, BlockEntity> getAttachedBlocks()
    {
        return menu.getAttachedBlocks();
    }

    public void drawEnergyBar(GuiGraphics graphics)
    {
        graphics.blit(GUI, getGuiLeft()+4, getGuiTop()+140, 0, 153, menu.getEnergyScaled(171), 7);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(Minecraft.getInstance().font, I18n.get("gui.block_booster_t3"), imageWidth/2, 4, 0xffffff );

        if(menu.isDisabled()) {
            graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.block_booster.disabled"), 10, 90, 0xffffff);
        }
    }

    private void drawAttachedBlocks(GuiGraphics graphics) {
        HashMap<Integer, BlockEntity> attachedBlocks = menu.getAttachedBlocks();
        List<Integer> sortedKeys = new ArrayList<>(attachedBlocks.keySet());
        sortedKeys.sort(Integer::compareTo);
        
        int displayIndex = 0;
        for(int i = scrollOffset; i < Math.min(scrollOffset + maxVisibleItems, sortedKeys.size()); i++) {
            Integer key = sortedKeys.get(i);
            BlockEntity be = attachedBlocks.get(key);
            if(be != null && !be.isRemoved()) {
                int blockX = getGuiLeft()+9;
                int blockY = getGuiTop()+14 + displayIndex*20;
                
                // Draw red frame if this is a slow block
                if(menu.isSlowBlock(key)) {
                    drawRedFrame(graphics, blockX, blockY, 16, 16);
                }
                
                graphics.renderItem(
                    new ItemStack(be.getBlockState().getBlock().defaultBlockState().getBlock().asItem()),
                    blockX, 
                    blockY
                );
            }
            displayIndex++;
        }
    }

    private void drawRedFrame(GuiGraphics graphics, int x, int y, int width, int height) {
        int frameColor = 0xFFFF0000; // Red color
        int thickness = 1;
        
        // Draw frame borders
        graphics.fill(x - thickness, y - thickness, x + width + thickness, y, frameColor); // Top
        graphics.fill(x - thickness, y + height, x + width + thickness, y + height + thickness, frameColor); // Bottom
        graphics.fill(x - thickness, y, x, y + height, frameColor); // Left
        graphics.fill(x + width, y, x + width + thickness, y + height, frameColor); // Right
    }

    private void drawScrollBar(GuiGraphics graphics) {
        if(totalItems <= maxVisibleItems) return;
        
        int scrollBarX = getGuiLeft() + 165;
        int scrollBarY = getGuiTop() + 14;
        int scrollBarHeight = maxVisibleItems * 20;
        int scrollBarWidth = 6;
        
        // Draw scroll track
        graphics.fill(scrollBarX, scrollBarY, scrollBarX + scrollBarWidth, scrollBarY + scrollBarHeight, 0xFF8B8B8B);
        
        // Calculate scroll thumb position and size
        int maxScroll = totalItems - maxVisibleItems;
        int thumbHeight = Math.max(20, scrollBarHeight * maxVisibleItems / totalItems);
        int thumbY = scrollBarY + (int)((scrollBarHeight - thumbHeight) * ((float)scrollOffset / maxScroll));
        
        // Draw scroll thumb
        graphics.fill(scrollBarX, thumbY, scrollBarX + scrollBarWidth, thumbY + thumbHeight, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if(totalItems > maxVisibleItems) {
            int oldOffset = scrollOffset;
            scrollOffset = Math.max(0, Math.min(totalItems - maxVisibleItems, scrollOffset - (int)delta));
            
            if(oldOffset != scrollOffset) {
                updateCheckboxes();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(totalItems > maxVisibleItems && button == 0) {
            int scrollBarX = getGuiLeft() + 165;
            int scrollBarY = getGuiTop() + 14;
            int scrollBarHeight = maxVisibleItems * 20;
            int scrollBarWidth = 6;
            
            if(mouseX >= scrollBarX && mouseX <= scrollBarX + scrollBarWidth &&
               mouseY >= scrollBarY && mouseY <= scrollBarY + scrollBarHeight) {
                
                int maxScroll = totalItems - maxVisibleItems;
                int thumbHeight = Math.max(20, scrollBarHeight * maxVisibleItems / totalItems);
                float scrollPercentage = (float)(mouseY - scrollBarY - thumbHeight/2) / (scrollBarHeight - thumbHeight);
                
                int oldOffset = scrollOffset;
                scrollOffset = Math.max(0, Math.min(maxScroll, (int)(scrollPercentage * maxScroll)));
                
                if(oldOffset != scrollOffset) {
                    updateCheckboxes();
                }
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
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