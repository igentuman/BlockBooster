package igentuman.blockbooster.client.screen;

import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.client.screen.element.CheckBox;
import igentuman.blockbooster.container.BoosterT3Container;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class BoosterT3Screen extends AbstractContainerScreen<BoosterT3Container> {

    private final Identifier GUI = Identifier.fromNamespaceAndPath(BlockBooster.MODID, "textures/gui/blockbooster_t3_gui.png");
    private final HashMap<Long, CheckBox> checkboxes = new HashMap<>();
    private int scrollOffset = 0;
    private int maxVisibleRows = 6;
    private int itemsPerRow = 2;
    private int totalItems = 0;

    public BoosterT3Screen(BoosterT3Container container, Inventory inv, Component name) {
        super(container, inv, name, 180, 152);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int pMouseX, int pMouseY, float pPartialTick) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, relX, relY, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);
        drawEnergyBar(graphics);
        drawAttachedBlocks(graphics);
        drawScrollBar(graphics);
        drawTPSIndicator(graphics);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int x, int y) {
        if (x > getGuiLeft() + 4 && x < getGuiLeft() + 175 && y > getGuiTop() + 139 && y < getGuiTop() + 149) {
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, Component.translatable("gui.energy.info", menu.getEnergy(), menu.getMaxEnergy()), x, y);
            return;
        }

        int tpsX = getGuiLeft() + 165;
        int tpsY = getGuiTop() + 4;
        if (x >= tpsX && x < tpsX + 8 && y >= tpsY && y < tpsY + 8) {
            List<Component> tooltip = new ArrayList<>();
            if (menu.isLagging()) {
                tooltip.add(Component.translatable("gui.tps.lagging"));
                tooltip.add(Component.translatable("gui.tps.current", String.format("%.1f", menu.getCurrentTPS())));
                tooltip.add(Component.translatable("gui.tps.boosting_paused"));
            } else {
                tooltip.add(Component.translatable("gui.tps.ok"));
                tooltip.add(Component.translatable("gui.tps.current", String.format("%.1f", menu.getCurrentTPS())));
            }
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, tooltip, Optional.empty(), x, y);
            return;
        }

        HashMap<Long, BlockEntity> attachedBlocks = menu.getAttachedBlocks();
        HashMap<Long, Long> boostTimes = menu.getBoostTimes();
        List<Long> sortedKeys = new ArrayList<>(attachedBlocks.keySet());
        sortedKeys.sort(Long::compareTo);

        int blockIconSize = 16;
        int blockSpacing = 20;
        int columnSpacing = 85;
        int startX = getGuiLeft() + 9;
        int startY = getGuiTop() + 14;

        int displayIndex = 0;
        int maxItemsVisible = maxVisibleRows * itemsPerRow;
        for (int i = scrollOffset; i < Math.min(scrollOffset + maxItemsVisible, sortedKeys.size()); i++) {
            Long key = sortedKeys.get(i);
            BlockEntity be = attachedBlocks.get(key);
            if (be != null && !be.isRemoved()) {
                int rowIndex = displayIndex / itemsPerRow;
                int colIndex = displayIndex % itemsPerRow;
                int blockX = startX + colIndex * columnSpacing;
                int blockY = startY + rowIndex * blockSpacing;

                if (x >= blockX && x < blockX + blockIconSize && y >= blockY && y < blockY + blockIconSize) {
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(Component.translatable(be.getBlockState().getBlock().getDescriptionId()));
                    tooltip.add(Component.literal(be.getBlockPos().toShortString()));

                    Long boostTime = boostTimes.get(key);
                    if (boostTime != null && boostTime > 0) {
                        double timeMs = boostTime / 1_000_000.0;
                        tooltip.add(Component.translatable("gui.boost.time", String.format("%.3f", timeMs)));
                        if (menu.isSlowBlock(key)) {
                            tooltip.add(Component.translatable("gui.boost.slow_block"));
                        }
                    }

                    graphics.setTooltipForNextFrame(Minecraft.getInstance().font, tooltip, Optional.empty(), x, y);
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
        for (CheckBox checkbox : checkboxes.values()) {
            removeWidget(checkbox);
        }
        this.checkboxes.clear();

        HashMap<Long, BlockEntity> attachedBlocks = menu.getAttachedBlocks();
        List<Long> sortedKeys = new ArrayList<>(attachedBlocks.keySet());
        sortedKeys.sort(Long::compareTo);

        totalItems = sortedKeys.size();

        int columnSpacing = 85;
        int blockSpacing = 20;
        int maxItemsVisible = maxVisibleRows * itemsPerRow;

        for (int i = 0; i < Math.min(maxItemsVisible, totalItems - scrollOffset); i++) {
            int actualIndex = i + scrollOffset;
            if (actualIndex < sortedKeys.size()) {
                long posKey = sortedKeys.get(actualIndex);
                int rowIndex = i / itemsPerRow;
                int colIndex = i % itemsPerRow;

                CheckBox checkbox = new CheckBox(
                    getGuiLeft() + 28 + colIndex * columnSpacing,
                    getGuiTop() + 15 + rowIndex * blockSpacing,
                    13, 13, 181, 0, 13, GUI,
                    (Button btn) -> this.checkboxClicked(btn, posKey)
                );
                this.checkboxes.put(posKey, checkbox);
                addRenderableWidget(checkbox);
            }
        }
    }

    public void checkboxClicked(Button btn, long posKey) {
        if (checkboxes == null) return;
        CheckBox checkBox = (CheckBox) btn;
        boolean val = !checkBox.isChecked();
        menu.checkboxClicked(posKey, val);
    }

    public void containerTick() {
        super.containerTick();

        int newTotalItems = menu.getAttachedBlocks().size();
        if (newTotalItems != totalItems) {
            totalItems = newTotalItems;
            int maxItemsVisible = maxVisibleRows * itemsPerRow;
            if (scrollOffset > Math.max(0, totalItems - maxItemsVisible)) {
                scrollOffset = Math.max(0, totalItems - maxItemsVisible);
            }
            updateCheckboxes();
        }

        HashMap<Long, Boolean> boostFlags = menu.getBoostFlags();
        for (long id : checkboxes.keySet()) {
            if (!checkboxes.containsKey(id)) continue;
            checkboxes.get(id).setChecked(boostFlags.getOrDefault(id, false));
        }
    }

    protected HashMap<Long, BlockEntity> getAttachedBlocks() {
        return menu.getAttachedBlocks();
    }

    public void drawEnergyBar(GuiGraphicsExtractor graphics) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, getGuiLeft() + 4, getGuiTop() + 140, 0.0f, 153.0f, menu.getEnergyScaled(171), 7, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.centeredText(Minecraft.getInstance().font, I18n.get("gui.block_booster_t3"), imageWidth / 2, 4, 0xffffff);
        if (menu.isDisabled()) {
            graphics.text(Minecraft.getInstance().font, Component.translatable("gui.block_booster.disabled"), 10, 90, 0xffffff);
        }
    }

    private void drawAttachedBlocks(GuiGraphicsExtractor graphics) {
        HashMap<Long, BlockEntity> attachedBlocks = menu.getAttachedBlocks();
        List<Long> sortedKeys = new ArrayList<>(attachedBlocks.keySet());
        sortedKeys.sort(Long::compareTo);

        int columnSpacing = 85;
        int blockSpacing = 20;
        int startX = getGuiLeft() + 9;
        int startY = getGuiTop() + 14;
        int maxItemsVisible = maxVisibleRows * itemsPerRow;

        int displayIndex = 0;
        for (int i = scrollOffset; i < Math.min(scrollOffset + maxItemsVisible, sortedKeys.size()); i++) {
            Long key = sortedKeys.get(i);
            BlockEntity be = attachedBlocks.get(key);
            if (be != null && !be.isRemoved()) {
                int rowIndex = displayIndex / itemsPerRow;
                int colIndex = displayIndex % itemsPerRow;
                int blockX = startX + colIndex * columnSpacing;
                int blockY = startY + rowIndex * blockSpacing;

                if (menu.isSlowBlock(key)) {
                    drawRedFrame(graphics, blockX, blockY, 16, 16);
                }

                graphics.item(new ItemStack(be.getBlockState().getBlock().defaultBlockState().getBlock().asItem()), blockX, blockY);
            }
            displayIndex++;
        }
    }

    private void drawRedFrame(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        int frameColor = 0xFFFF0000;
        int thickness = 1;
        graphics.fill(x - thickness, y - thickness, x + width + thickness, y, frameColor);
        graphics.fill(x - thickness, y + height, x + width + thickness, y + height + thickness, frameColor);
        graphics.fill(x - thickness, y, x, y + height, frameColor);
        graphics.fill(x + width, y, x + width + thickness, y + height, frameColor);
    }

    private void drawScrollBar(GuiGraphicsExtractor graphics) {
        int maxItemsVisible = maxVisibleRows * itemsPerRow;
        if (totalItems <= maxItemsVisible) return;

        int scrollBarX = getGuiLeft() + 165;
        int scrollBarY = getGuiTop() + 14;
        int scrollBarHeight = maxVisibleRows * 20;
        int scrollBarWidth = 6;

        graphics.fill(scrollBarX, scrollBarY, scrollBarX + scrollBarWidth, scrollBarY + scrollBarHeight, 0xFF8B8B8B);

        int maxScroll = totalItems - maxItemsVisible;
        int thumbHeight = Math.max(20, scrollBarHeight * maxItemsVisible / totalItems);
        int thumbY = scrollBarY + (int) ((scrollBarHeight - thumbHeight) * ((float) scrollOffset / maxScroll));

        graphics.fill(scrollBarX, thumbY, scrollBarX + scrollBarWidth, thumbY + thumbHeight, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxItemsVisible = maxVisibleRows * itemsPerRow;
        if (totalItems > maxItemsVisible) {
            int oldOffset = scrollOffset;
            scrollOffset = Math.max(0, Math.min(totalItems - maxItemsVisible, scrollOffset - (int) (scrollY * itemsPerRow)));
            if (oldOffset != scrollOffset) {
                updateCheckboxes();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        int maxItemsVisible = maxVisibleRows * itemsPerRow;
        if (totalItems > maxItemsVisible && button == 0) {
            int scrollBarX = getGuiLeft() + 165;
            int scrollBarY = getGuiTop() + 14;
            int scrollBarHeight = maxVisibleRows * 20;
            int scrollBarWidth = 6;

            if (mouseX >= scrollBarX && mouseX <= scrollBarX + scrollBarWidth &&
                mouseY >= scrollBarY && mouseY <= scrollBarY + scrollBarHeight) {

                int maxScroll = totalItems - maxItemsVisible;
                int thumbHeight = Math.max(20, scrollBarHeight * maxItemsVisible / totalItems);
                float scrollPercentage = (float) (mouseY - scrollBarY - thumbHeight / 2) / (scrollBarHeight - thumbHeight);

                int oldOffset = scrollOffset;
                scrollOffset = Math.max(0, Math.min(maxScroll, (int) (scrollPercentage * maxScroll)));
                if (oldOffset != scrollOffset) {
                    updateCheckboxes();
                }
                return true;
            }
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }

    private void drawTPSIndicator(GuiGraphicsExtractor graphics) {
        int x = getGuiLeft() + 165;
        int y = getGuiTop() + 4;
        int size = 8;
        int color = menu.isLagging() ? 0xFFFF0000 : 0xFF00FF00;
        graphics.fill(x, y, x + size, y + size, color);
        graphics.fill(x, y, x + size, y + 1, 0xFF000000);
        graphics.fill(x, y + size - 1, x + size, y + size, 0xFF000000);
        graphics.fill(x, y, x + 1, y + size, 0xFF000000);
        graphics.fill(x + size - 1, y, x + size, y + size, 0xFF000000);
    }
}
