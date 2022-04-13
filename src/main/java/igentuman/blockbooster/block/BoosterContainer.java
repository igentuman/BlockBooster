package igentuman.blockbooster.block;

import igentuman.blockbooster.setup.Registration;
import igentuman.blockbooster.tile.BoosterBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class BoosterContainer extends AbstractContainerMenu {

    private BoosterBE blockEntity;
    private Player playerEntity;

    public BoosterContainer(int windowId, BlockPos pos, Inventory playerInventory, Player player) {
        super(Registration.BLOCKBOOSTER_CONTAINER.get(), windowId);
        blockEntity = (BoosterBE)player.getCommandSenderWorld().getBlockEntity(pos);
        this.playerEntity = player;
    }

    public String getTopBlock()
    {
        return blockEntity.topBlock;
    }

    public String getBottomBlock()
    {
        return blockEntity.bottomBlock;
    }

    public String getLeftBlock()
    {
        return blockEntity.leftBlock;
    }

    public String getRightBlock()
    {
        return blockEntity.rightBlock;
    }

    public int getEnergy() {
        return blockEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), playerEntity, Registration.BLOCKBOOSTER.get());
    }

}
