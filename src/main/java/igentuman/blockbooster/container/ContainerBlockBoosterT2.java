package igentuman.blockbooster.container;

import igentuman.blockbooster.tile.TileBlockBoosterT2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ContainerBlockBoosterT2 extends Container {
    private final TileBlockBoosterT2 booster;

    public ContainerBlockBoosterT2(TileBlockBoosterT2 booster) {
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

    public TileEntity getBooster() {
        return booster;
    }

    public byte getCheckboxValue(int id) {
        if(booster.getBoostFlag().length < id+1) return 0;
        return booster.getBoostFlag()[id];
    }
}
