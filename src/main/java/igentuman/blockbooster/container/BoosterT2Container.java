package igentuman.blockbooster.container;

import igentuman.blockbooster.network.BoosterPacket;
import igentuman.blockbooster.setup.Messages;
import igentuman.blockbooster.setup.Registration;
import igentuman.blockbooster.tile.TileBoosterT2;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;

public class BoosterT2Container extends AbstractContainerMenu {

    private final TileBoosterT2 blockEntity;
    private final Player playerEntity;

    // Constructor for network instantiation (called by MenuType factory)
    public BoosterT2Container(int windowId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(Registration.BLOCKBOOSTER_T2_CONTAINER.get(), windowId);
        Player player = playerInventory.player;
        BlockPos pos = buf.readBlockPos();
        blockEntity = (TileBoosterT2)player.level().getBlockEntity(pos);
        this.playerEntity = player;
    }

    // Constructor for server-side instantiation
    public BoosterT2Container(int windowId, BlockPos pos, Inventory playerInventory) {
        super(Registration.BLOCKBOOSTER_T2_CONTAINER.get(), windowId);
        Player player = playerInventory.player;
        blockEntity = (TileBoosterT2)player.level().getBlockEntity(pos);
        this.playerEntity = player;
    }

    public BoosterT2Container(int windowId, Inventory playerInventory, BlockPos pos) {
        super(Registration.BLOCKBOOSTER_T2_CONTAINER.get(), windowId);
        blockEntity = (TileBoosterT2)playerInventory.player.level().getBlockEntity(pos);
        this.playerEntity = playerInventory.player;
    }

    public int getEnergyScaled(int scale)
    {
        return (int) (scale*((float)blockEntity.getEnergy()/(float)blockEntity.getMaxEnergy()));
    }

    public boolean isDisabled()
    {
        return blockEntity.isDisabled;
    }

    public boolean isLagging()
    {
        return blockEntity.isLagging();
    }

    public double getCurrentTPS()
    {
        return blockEntity.getCurrentTPS();
    }

    public int getEnergy() {
        // NeoForge 1.21: No getCapability method, use direct access instead
        return blockEntity != null ? blockEntity.getEnergy() : 0;
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

    public HashMap<Long, BlockEntity> getAttachedBlocks() {
        return blockEntity.getAttachedBlocks();
    }

    public boolean isChecked(int i) {
        return blockEntity.isIndexEnabled(i);
    }

    public void checkboxClicked(long id, boolean val) {
        Messages.sendToServer(new BoosterPacket(blockEntity.getBlockPos(), id, val));
    }

    public HashMap<Long, Boolean> getBoostFlags() {
        return blockEntity.getBoostFlags();
    }
}
