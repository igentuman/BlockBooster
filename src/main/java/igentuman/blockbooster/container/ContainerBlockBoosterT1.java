package igentuman.blockbooster.container;

import igentuman.blockbooster.tile.TileBlockBoosterT1;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ContainerBlockBoosterT1 extends Container {
    private final TileBlockBoosterT1 booster;

    public ContainerBlockBoosterT1(TileBlockBoosterT1 booster) {
        this.booster = booster;
    }

    public int getEnergyStored()
    {
        return booster.getEnergyStored();
    }

    public boolean isWorking()
    {
        return booster.isWorking();
    }

    @Override
    public boolean canInteractWith(@NotNull EntityPlayer playerIn) {
        return true;
    }

    public HashMap<Integer, TileEntity> getAttachedBlocks() {
        return booster.getAttachedBlocks();
    }

    public int getEnergyScaled(int scale)
    {
        return (int) (scale*((float)booster.getEnergyStored()/(float)booster.getMaxEnergyStored()));
    }
}
