package igentuman.blockbooster.client.screen;

import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.client.screen.element.CheckBox;
import igentuman.blockbooster.container.BoosterT1Container;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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

public class BoosterT1Screen extends AbstractContainerScreen<BoosterT1Container> {

    private final Identifier GUI = Identifier.fromNamespaceAndPath(BlockBooster.MODID, "textures/gui/blockbooster_gui.png");
    private final HashMap<Long, CheckBox> checkboxes = new HashMap<>();

    public BoosterT1Screen(BoosterT1Container container, Inventory inv, Component name) {
        super(container, inv, name, 180, 152);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int pMouseX, int pMouseY, float pPartialTick) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, relX, relY, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);
        drawEnergyBar(graphics);
        drawAttachedBlocks(graphics);
        drawTPSIndicator(graphics);
        super.extractRenderState(graphics, pMouseX, pMouseY, pPartialTick);
    }

    protected void init() {
        super.init();
        this.checkboxes.clear();
        int index = 0;
        for (long id : menu.getBoostFlags().keySet()) {
            checkboxes.put(id, new CheckBox(getGuiLeft() + 28, getGuiTop() + 15 + index * 20, 13, 13, 181, 0, 13, GUI, (Button btn) -> this.checkboxClicked(btn, id)));
            checkboxes.get(id).setChecked(menu.getBoostFlags().get(id));
            index++;
        }
        for (Button btn : checkboxes.values()) {
            addRenderableWidget(btn);
        }
    }

    public void checkboxClicked(Button btn, long id) {
        if (checkboxes == null) return;
        CheckBox checkBox = checkboxes.get(id);
        if (checkBox == null) return;
        boolean val = !checkBox.isChecked();
        menu.checkboxClicked(id, val);
    }

    public void containerTick() {
        super.containerTick();
        for (long id : menu.getBoostFlags().keySet()) {
            if (!checkboxes.containsKey(id)) continue;
            checkboxes.get(id).setChecked(menu.getBoostFlags().get(id));
        }
    }

    protected HashMap<Long, BlockEntity> getAttachedBlocks() {
        return menu.getAttachedBlocks();
    }

    public void drawEnergyBar(GuiGraphicsExtractor graphics) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, getGuiLeft() + 4, getGuiTop() + 67, 0.0f, 153.0f, menu.getEnergyScaled(171), 7, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.centeredText(Minecraft.getInstance().font, I18n.get("gui.block_booster"), imageWidth / 2, 4, 0xffffff);
        if (menu.isDisabled()) {
            graphics.text(Minecraft.getInstance().font, Component.translatable("gui.block_booster.disabled"), 10, 65, 0xffffff);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int x, int y) {
        if (x > getGuiLeft() + 4 && x < getGuiLeft() + 175 && y > getGuiTop() + 65 && y < getGuiTop() + 78) {
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
        int blockIconX = getGuiLeft() + 9;
        int blockIconSize = 16;
        int blockSpacing = 20;
        int startY = getGuiTop() + 14;

        int index = 0;
        for (Long posKey : attachedBlocks.keySet()) {
            BlockEntity be = attachedBlocks.get(posKey);
            if (be != null && !be.isRemoved()) {
                int blockY = startY + index * blockSpacing;
                if (x >= blockIconX && x < blockIconX + blockIconSize && y >= blockY && y < blockY + blockIconSize) {
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(Component.translatable(be.getBlockState().getBlock().getDescriptionId()));
                    tooltip.add(Component.literal(be.getBlockPos().toShortString()));
                    graphics.setTooltipForNextFrame(Minecraft.getInstance().font, tooltip, Optional.empty(), x, y);
                    return;
                }
                index++;
            }
        }
    }

    private void drawAttachedBlocks(GuiGraphicsExtractor graphics) {
        int y = 14;
        int index = 0;
        for (Long posKey : menu.getAttachedBlocks().keySet()) {
            BlockEntity be = menu.getAttachedBlocks().get(posKey);
            if (be != null) {
                graphics.item(new ItemStack(be.getBlockState().getBlock().asItem()), getGuiLeft() + 9, getGuiTop() + index * 20 + y);
                index++;
            }
        }
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
