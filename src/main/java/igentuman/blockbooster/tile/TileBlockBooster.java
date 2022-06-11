package igentuman.blockbooster.tile;

import igentuman.blockbooster.network.ModPacketHandler;
import igentuman.blockbooster.network.TileProcessUpdatePacket;

import nc.ModCheck;
import nc.config.NCConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.function.Predicate;

import static igentuman.blockbooster.ModConfig.boosterConfig;


public class TileBlockBooster  extends TileEntity implements ITickable, IEnergyStorage {

    private final EnergyStorage storage;

    public boolean isRedstonePowered() {
        return isRedstonePowered;
    }

    public boolean isWorking() {
        return isWorking;
    }

    private boolean isRedstonePowered = false;
    private boolean isWorking = false;

    public TileEntity getTopTe() {
        return topTe;
    }

    public TileEntity getBottomTe() {
        return bottomTe;
    }

    protected TileEntity topTe;
    protected TileEntity bottomTe;

    public TileBlockBooster() {
        this(boosterConfig.rf_per_tick*20, boosterConfig.rf_per_tick*20);
    }

    public TileBlockBooster(int capacity, int maxTransfer) {
        this.storage = new EnergyStorage(capacity, maxTransfer);
    }


    public EnergyStorage getEnergyStorage() {
        return this.storage;
    }

    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        return capability == CapabilityEnergy.ENERGY;
    }

    boolean hasEnergySideCapability(@Nullable EnumFacing side) {
        return true;
    }

    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) {
        if (capability == CapabilityEnergy.ENERGY) {
            return this.hasEnergySideCapability(side) ? CapabilityEnergy.ENERGY.cast(storage) : null;
        }  else {
            return super.getCapability(capability, side);
        }
    }

    public void setEnergyStored(int amount)
    {
        storage.extractEnergy(getEnergyStored(), false);
        storage.receiveEnergy(amount, false);
    }

    private boolean isAllowedToBoost(TileEntity te) {
        boolean goodTE = te instanceof ITickable && !(te instanceof TileBlockBooster);
        if(!goodTE) return false;
        if(boosterConfig.white_list.length > 0) {
            return Arrays.stream(boosterConfig.white_list).anyMatch(str -> te.getDisplayName().getUnformattedText().equals(str));
        }
        if(boosterConfig.black_list.length > 0) {
            return Arrays.stream(boosterConfig.black_list).noneMatch(str -> te.getDisplayName().getUnformattedText().equals(str));
        }
        return true;
    }

    @Override
    public void update() {

        topTe = world.getTileEntity(getPos().add(0,1,0));
        bottomTe = world.getTileEntity(getPos().add(0,-1,0));
        if(world.isRemote) return;
        if(world.getRedstonePowerFromNeighbors(getPos()) > 0 && boosterConfig.deactivate_with_redstone) {
            if(isWorking) {
                isWorking = false;
                ModPacketHandler.instance.sendToAll(this.getTileUpdatePacket());

            }
            return;
        }
        if (getEnergyStored() < boosterConfig.rf_per_tick) return;
        boolean wasWorking = isWorking;
        if(topTe != null && boosterConfig.boost_on_top && isAllowedToBoost(topTe)) {
            for(int i = 0; i < boosterConfig.boost_rate; i++) {
                if (getEnergyStored() < boosterConfig.rf_per_tick) return;
                isWorking = true;
                ((ITickable) topTe).update();
                storage.extractEnergy(boosterConfig.rf_per_tick, false);
            }
        }

        if(bottomTe != null && boosterConfig.boost_on_bottom && isAllowedToBoost(bottomTe)) {
            for(int i = 0; i < boosterConfig.boost_rate; i++) {
                if (getEnergyStored() < boosterConfig.rf_per_tick) return;
                isWorking = true;
                ((ITickable) bottomTe).update();
                storage.extractEnergy(boosterConfig.rf_per_tick, false);
            }
        }
        if(isWorking || (isWorking != wasWorking)) {
            if( !world.isRemote) {
                ModPacketHandler.instance.sendToAll(this.getTileUpdatePacket());
            }
            markDirty();
        }
    }

    public TileProcessUpdatePacket getTileUpdatePacket() {
        return new TileProcessUpdatePacket(
                this.pos,
                getEnergyStored(),
                isWorking,
                isRedstonePowered
        );
    }

    public void onTileUpdatePacket(TileProcessUpdatePacket message)
    {
        setEnergyStored(message.energyStored);
        isWorking = message.isWorking;
        isRedstonePowered = message.isRedstonePowered;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if(getEnergyStored() < getMaxEnergyStored()) {

            int received = storage.receiveEnergy(maxReceive, simulate);
            if( !world.isRemote) {
                ModPacketHandler.instance.sendToAll(this.getTileUpdatePacket());
            }
            return received;
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return storage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @NotNull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("isWorking", isWorking);
        compound.setBoolean("isRedstonePowered", isRedstonePowered);
        compound.setInteger("energyStored", getEnergyStored());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        isWorking = compound.getBoolean("isWorking");
        isRedstonePowered = compound.getBoolean("isRedstonePowered");
        setEnergyStored(compound.getInteger("energyStored"));
    }
}
