package igentuman.blockbooster.container;

import igentuman.blockbooster.network.BoosterPacket;
import igentuman.blockbooster.setup.Messages;
import igentuman.blockbooster.setup.Registration;
import igentuman.blockbooster.tile.TileBoosterT2;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.HashMap;

public class BoosterT2Container extends AbstractContainerMenu {

    private TileBoosterT2 blockEntity;
    private Player playerEntity;

    public BoosterT2Container(int windowId, BlockPos pos, Inventory playerInventory, Player player) {
        super(Registration.BLOCKBOOSTER_T2_CONTAINER.get(), windowId);
        blockEntity = (TileBoosterT2)player.getCommandSenderWorld().getBlockEntity(pos);
        this.playerEntity = player;
    }

    public int getEnergyScaled(int scale)
    {
        return (int) (scale*((float)blockEntity.getEnergy()/(float)blockEntity.getMaxEnergy()));
    }

    public boolean isDisabled()
    {
        return blockEntity.isDisabled;
    }


    public int getEnergy() {
        return blockEntity.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public int getMaxEnergy() {
        return blockEntity.getMaxEnergy();
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return null;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), playerEntity, Registration.BLOCKBOOSTER_T2.get());
    }

    public HashMap<Integer, BlockEntity> getAttachedBlocks() {
        return blockEntity.getAttachedBlocks();
    }

    public boolean isChecked(int i) {
        return blockEntity.isIndexEnabled(i);
    }

    public void checkboxClicked(int id, int val) {
        Messages.sendToServer(new BoosterPacket(blockEntity.getBlockPos(), id, (byte) val));
    }

    public byte[] getBoostFlags() {
        return blockEntity.getBoostFlag();
    }
}
