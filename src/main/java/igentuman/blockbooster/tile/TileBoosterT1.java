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

public class TileBoosterT1 extends AbstractBooster {

    private final CustomEnergyStorage energy = createEnergyStorage();
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy);
    public int fePerTick = CommonConfig.GENERAL.t1_fe_per_tick.get();

    public TileBoosterT1(BlockPos pos, BlockState state) {
        super(Registration.BLOCKBOOSTER_T1_BE.get(), pos, state);
        this.boostFlag = new byte[]{0, 0}; // T1 only has 2 directions (up/down)
    }

    @Override
    protected boolean shouldSkipDirection(Direction direction) {
        // T1 only scans up and down (ordinal 0 and 1)
        return direction.ordinal() > 1;
    }

/*  for testing to simulate lags
    @Override
    public void tickServer() {
        for(int x = 0; x < 20; x++) {
            for(int z = 0; z < 50; z++) {
                for(int y = 0; y < 20; y++) {
                    level.getBlockEntity(worldPosition.offset(x,y,z));
                    level.getBlockState(worldPosition.offset(x,y,z));
                }
            }
        }
    }*/

    @Override
    protected boolean canBoost() {
        return getEnergy() >= fePerTick;
    }

    @Override
    protected int getBoostRate() {
        return CommonConfig.GENERAL.t1_boost_rate.get();
    }

    @Override
    protected void consumeResource() {
        consumeEnergy(fePerTick);
    }

    @Override
    protected void saveBoosterData(CompoundTag tag) {
        tag.put("Energy", energy.serializeNBT());
    }

    @Override
    protected void loadBoosterData(CompoundTag tag) {
        if (tag.contains("Energy")) {
            energy.deserializeNBT(tag.get("Energy"));
        }
    }

    public int getEnergy() {
        return energy.getEnergyStored();
    }

    public void consumeEnergy(int amount) {
        energy.consumeEnergy(amount);
    }

    public int getMaxEnergy() {
        return CommonConfig.GENERAL.t1_fe_per_tick.get() * 100;
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
