package igentuman.blockbooster.gui;

import igentuman.blockbooster.ModInfo;
import igentuman.blockbooster.container.ContainerBlockBoosterT2;
import igentuman.blockbooster.gui.element.Checkbox;
import igentuman.blockbooster.network.ModPacketHandler;
import igentuman.blockbooster.network.SimpleCommandToServerPacket;
import igentuman.blockbooster.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuiBlockBoosterT2 extends GuiContainer {
    private static final ResourceLocation background = new ResourceLocation(
            ModInfo.MODID, "textures/gui/container/booster_t2.png"
    );
    private List<Checkbox> checkboxes = new ArrayList<>();

    private final ContainerBlockBoosterT2 container;

    public GuiBlockBoosterT2(ContainerBlockBoosterT2 inventorySlotsIn) {
        super(inventorySlotsIn);
        this.container = inventorySlotsIn;
        xSize=180;
        ySize=152;
    }

    public void initGui()
    {
        super.initGui();
        checkboxes = new ArrayList<>();
        checkboxes.add(new Checkbox(0,guiLeft+28,guiTop+15,13, 13, 181, 0, 13, background));
        checkboxes.add(new Checkbox(1,guiLeft+28,guiTop+35,13, 13, 181, 0, 13, background));
        checkboxes.add(new Checkbox(2,guiLeft+28,guiTop+55,13, 13, 181, 0, 13, background));
        checkboxes.add(new Checkbox(3,guiLeft+28,guiTop+75,13, 13, 181, 0, 13, background));
        checkboxes.add(new Checkbox(4,guiLeft+28,guiTop+95,13, 13, 181, 0, 13, background));
        checkboxes.add(new Checkbox(5,guiLeft+28,guiTop+115,13, 13, 181, 0, 13, background));
        for (Checkbox btn: checkboxes) {
            buttonList.add(btn);
        }
    }

    public void actionPerformed(GuiButton btn)
    {
        if(checkboxes.contains(btn)) {
            checkboxes.get(btn.id).toggleCheck();
            ModPacketHandler.
                    instance.
                    sendToServer(
                    new SimpleCommandToServerPacket(container.getBooster().getPos(), btn.id, (byte) (checkboxes.get(btn.id).isChecked() ? 1:0)));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        drawEnergyBar();
        drawAttachedBlocks();
        updateCheckboxes();
    }

    private void updateCheckboxes() {
        for(Checkbox checkbox: checkboxes) {
            checkbox.setChecked(container.getCheckboxValue(checkbox.id) == 1);
        }
    }

    protected HashMap<Integer, TileEntity> getAttachedBlocks()
    {
        return  container.getAttachedBlocks();
    }

    public void drawEnergyBar()
    {
        drawTexturedModalRect(guiLeft+4, guiTop+140, 0, 153, container.getEnergyScaled(171), 7);
    }

    public ItemStack getItemStackFromTile(TileEntity te)
    {
        IBlockState actualState = te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos());
        Block block = te.getBlockType().getBlockState().getBlock();

        int id = Block.getIdFromBlock(block);
        int meta = block.getMetaFromState(actualState);
        return new ItemStack(block, 1, meta);
    }

    private void drawAttachedBlocks() {
        int y = 13;
        if(getAttachedBlocks().size() < 1) return;
        for(Integer i: getAttachedBlocks().keySet()) {
            ClientUtil.renderItem(guiLeft+9, guiTop+i*20+y, getItemStackFromTile(getAttachedBlocks().get(i))
            );
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)this.guiLeft, (float)this.guiTop, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();

        GlStateManager.popMatrix();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(I18n.format("booster_t2.container.title"), 8, 4, 4210752);
    }
}
