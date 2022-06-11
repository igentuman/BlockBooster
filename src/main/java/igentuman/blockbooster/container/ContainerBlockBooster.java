package igentuman.blockbooster.container;

import igentuman.blockbooster.tile.TileBlockBooster;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import org.jetbrains.annotations.NotNull;

public class ContainerBlockBooster extends Container {
    private final TileBlockBooster booster;

    public ContainerBlockBooster(TileBlockBooster booster) {
        this.booster = booster;
    }

    public int getEnergyStored()
    {
        return booster.getEnergyStored();
    }


    public String getTopTe()
    {
        if(booster.getTopTe() == null) return "";
        return I18n.format(booster.getTopTe().getDisplayName().getUnformattedText());
    }

    public boolean isWorking()
    {
        return booster.isWorking();
    }

    public String getBottomTe()
    {
        if(booster.getBottomTe() == null) return "";
        return I18n.format(booster.getBottomTe().getDisplayName().getUnformattedText());
    }

    public String debugBottomTe()
    {
        if(booster.getBottomTe() == null) return "";
        String registryName = booster.getBottomTe().getBlockType().getRegistryName().toString();
        return registryName + ":"+booster.getBottomTe().getBlockMetadata();
    }

    public String debugTopTe()
    {
        if(booster.getTopTe() == null) return "";
        String registryName = booster.getTopTe().getBlockType().getRegistryName().toString();
        return registryName + ":"+booster.getTopTe().getBlockMetadata();
    }

    @Override
    public boolean canInteractWith(@NotNull EntityPlayer playerIn) {
        return true;
    }

}
