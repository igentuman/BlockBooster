package igentuman.blockbooster.tile;

import igentuman.blockbooster.network.ModPacketHandler;
import igentuman.blockbooster.network.TileBoosterUpdatePacket;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.HashMap;

import static igentuman.blockbooster.ModConfig.boosterT1Config;
import static igentuman.blockbooster.ModConfig.general;

public class TileBlockBoosterT1 extends TileEntity implements ITickable, IEnergyStorage, ITileBooster {

    private final EnergyStorage storage;
    private int updateCounter = 20;

    public boolean isRedstonePowered() {
        return isRedstonePowered;
    }

    private boolean isRedstonePowered = false;
    private boolean isWorking = false;
    private HashMap<Integer, TileEntity> attachedBlocks = new HashMap<>();

    public byte[] getBoostFlag() {
        return boostFlag;
    }

    private byte[] boostFlag = new byte[] {0,0};

    public boolean isWorking() {
        return isWorking;
    }

    public TileBlockBoosterT1() {
        this(boosterT1Config.rf_per_tick*50, boosterT1Config.rf_per_tick*50);
    }

    public TileBlockBoosterT1(int capacity, int maxTransfer) {
        this.storage = new EnergyStorage(capacity, maxTransfer);
    }

    public void updateAttachedBlocks()
    {
        updateCounter--;
        if(updateCounter <= 0) {
            updateCounter = 40;
        }
        boolean changed = false;
        for(EnumFacing direction: EnumFacing.values()) {
            if(direction.ordinal() > 1) continue;
            TileEntity te = world.getTileEntity(new BlockPos(getPos().offset(direction, 1)));
            boolean contains = attachedBlocks.containsKey(direction.ordinal());
            if(te == null) {
                if(contains) {
                    changed = true;
                    attachedBlocks.remove(direction.ordinal());
                }
                continue;
            }
            if(contains && attachedBlocks.get(direction.ordinal()).equals(te)) continue;
            if(!isAllowedToBoost(te)) continue;
            if(contains) {
                attachedBlocks.remove(direction.ordinal());
            }
            changed = true;
            attachedBlocks.put(direction.ordinal(), te);
        }
        if(changed) {
            markDirty();
        }
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
        boolean goodTE = te instanceof ITickable && !(te instanceof TileBlockBoosterT1);
        if(!goodTE) return false;
        String blockName = getBlockDataInfo(te.getPos());
        if(general.white_list.length > 0) {
            return Arrays.stream(general.white_list).anyMatch(str -> blockName.equals(str));
        }
        if(general.black_list.length > 0) {
            return Arrays.stream(general.black_list).noneMatch(str -> blockName.equals(str));
        }
        return true;
    }


    public String getBlockDataInfo(BlockPos pos)
    {
        IBlockState actualState = world.getBlockState(pos).getActualState(world, pos);
        Block block = actualState.getBlock();

        int id = Block.getIdFromBlock(block);
        int meta = block.getMetaFromState(actualState);
        ResourceLocation rl = ForgeRegistries.BLOCKS.getKey(block);
        String registryName = rl != null ? rl.toString() : "<null>";
        return registryName+":"+id+":"+meta;
    }

    @Override
    public void update() {

        updateAttachedBlocks();
        if(world.isRemote) return;
        if(world.getRedstonePowerFromNeighbors(getPos()) > 0 && general.deactivate_with_redstone) {
            if(isWorking) {
                isWorking = false;
                ModPacketHandler.instance.sendToAll(this.getTileUpdatePacket());
            }
            return;
        }
        if (getEnergyStored() < boosterT1Config.rf_per_tick) return;
        boolean wasWorking = isWorking;
        isWorking = boostBlocks();
        if(isWorking || (isWorking != wasWorking)) {
            if( !world.isRemote) {
                ModPacketHandler.instance.sendToAll(this.getTileUpdatePacket());
            }
            markDirty();
        }
    }

    private boolean boostBlocks() {
        boolean boosted = false;
        for(Integer id: attachedBlocks.keySet()) {
            if(id > boostFlag.length-1) break;
            if(boostFlag[id] == 0) continue;
            TileEntity te = attachedBlocks.get(id);
            if(te == null || getEnergyStored() < boosterT1Config.rf_per_tick) return boosted;
            try {
                for (int i = 0; i < boosterT1Config.boost_rate; i++) {
                    ((ITickable)te).update();
                    consumeEnergy();
                    boosted = true;
                }
            } catch (NullPointerException ignored) {

            }
        }
        return boosted;
    }

    private void consumeEnergy() {
        storage.extractEnergy(boosterT1Config.rf_per_tick, false);
    }

    public TileBoosterUpdatePacket getTileUpdatePacket() {
        return new TileBoosterUpdatePacket(
                this.pos,
                getEnergyStored(),
                isWorking,
                isRedstonePowered,
                boostFlag,
                2
        );
    }

    public void onTileUpdatePacket(TileBoosterUpdatePacket message)
    {
        setEnergyStored(message.energyStored);
        isWorking = message.isWorking;
        isRedstonePowered = message.isRedstonePowered;
        boostFlag = message.boostFLag;
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
        return false;
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
        compound.setByteArray("boostFlag", boostFlag);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        isWorking = compound.getBoolean("isWorking");
        isRedstonePowered = compound.getBoolean("isRedstonePowered");
        if(compound.getByteArray("boostFlag").length == 2) {
            boostFlag = compound.getByteArray("boostFlag");
        }
        setEnergyStored(compound.getInteger("energyStored"));
    }

    public HashMap<Integer, TileEntity> getAttachedBlocks() {
        return attachedBlocks;
    }

    @Override
    public void setBoostFlagValue(int id, byte val) {
        boostFlag[id] = val;
        markDirty();
        ModPacketHandler.instance.sendToAll(this.getTileUpdatePacket());
    }
}
