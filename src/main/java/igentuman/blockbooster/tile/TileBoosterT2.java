package igentuman.blockbooster.tile;

import igentuman.blockbooster.config.CommonConfig;
import igentuman.blockbooster.setup.Registration;
import igentuman.blockbooster.util.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileBoosterT2 extends AbstractBooster {

    private final CustomEnergyStorage energy = createEnergyStorage();
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy);
    public int fePerTick = CommonConfig.GENERAL.t2_fe_per_tick.get();

    public TileBoosterT2(BlockPos pos, BlockState state) {
        super(Registration.BLOCKBOOSTER_T2_BE.get(), pos, state);
    }

    @Override
    protected boolean canBoost() {
        return getEnergy() >= fePerTick;
    }

    @Override
    protected int getBoostRate() {
        return CommonConfig.GENERAL.t2_boost_rate.get();
    }

    @Override
    protected long consumeResource() {
        consumeEnergy(fePerTick);
        return fePerTick;
    }

    @Override
    public String getResourceUnit() {
        return "FE";
    }

    @Override
    protected void saveBoosterData(CompoundTag tag) {
        tag.put("Energy", energy.serializeNBT());
        
        // Save boost flags for direction-based indexing
        CompoundTag flagsTag = new CompoundTag();
        for (Long key : boostFlags.keySet()) {
            flagsTag.putBoolean(String.valueOf(key), boostFlags.get(key));
        }
        tag.put("boostFlags", flagsTag);
    }

    @Override
    protected void loadBoosterData(CompoundTag tag) {
        if (tag.contains("Energy")) {
            energy.deserializeNBT(tag.get("Energy"));
        }
        
        // Load boost flags
        if (tag.contains("boostFlags")) {
            boostFlags.clear();
            CompoundTag flagsTag = tag.getCompound("boostFlags");
            for (String key : flagsTag.getAllKeys()) {
                boostFlags.put(Long.parseLong(key), flagsTag.getBoolean(key));
            }
        }
    }

    public int getEnergy() {
        return energy.getEnergyStored();
    }

    public void consumeEnergy(int amount) {
        energy.consumeEnergy(amount);
    }

    public int getMaxEnergy() {
        return CommonConfig.GENERAL.t2_fe_per_tick.get() * 100;
    }

    private CustomEnergyStorage createEnergyStorage() {
        return new CustomEnergyStorage(
                getMaxEnergy(),
                getMaxEnergy()
        ) {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                int rc = super.receiveEnergy(maxReceive, simulate);
                if (rc > 0 && !simulate) {
                    setChanged();
                }
                return rc;
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}
