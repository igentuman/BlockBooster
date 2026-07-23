package igentuman.blockbooster.tile;

import igentuman.blockbooster.config.CommonConfig;
import igentuman.blockbooster.setup.Registration;
import igentuman.blockbooster.util.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;

public class TileBoosterT1 extends AbstractBooster {

    public final CustomEnergyStorage energy = createEnergyStorage();
    public int fePerTick = CommonConfig.GENERAL.t1_fe_per_tick.get();

    public TileBoosterT1(BlockPos pos, BlockState state) {
        super(igentuman.blockbooster.setup.Registration.BLOCKBOOSTER_T1_BE.get(), pos, state);
    }

    @Override
    protected boolean shouldSkipDirection(Direction direction) {
        // T1 only scans up and down (ordinal 0 and 1)
        return direction.ordinal() > 1;
    }

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
    protected void saveBoosterData(ValueOutput output) {
        output.putInt("Energy", energy.getEnergyStored());
        ValueOutput flagsOut = output.child("boostFlags");
        for (Long key : boostFlags.keySet()) {
            flagsOut.putBoolean(String.valueOf(key), boostFlags.get(key));
        }
    }

    @Override
    protected void loadBoosterData(ValueInput input) {
        energy.setEnergy(input.getIntOr("Energy", 0));
        boostFlags.clear();
        input.read("boostFlags", CompoundTag.CODEC).ifPresent(flagsTag -> {
            for (String key : flagsTag.keySet()) {
                boostFlags.put(Long.parseLong(key), flagsTag.getBooleanOr(key, false));
            }
        });
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
}
