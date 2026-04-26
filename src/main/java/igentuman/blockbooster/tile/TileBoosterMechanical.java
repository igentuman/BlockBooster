package igentuman.blockbooster.tile;

import igentuman.blockbooster.config.CommonConfig;
import igentuman.blockbooster.setup.Registration;
import igentuman.blockbooster.util.BoosterUtil;
import igentuman.blockbooster.util.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

public class TileBoosterMechanical extends AbstractBooster {

    private final CustomEnergyStorage energy = createEnergyStorage();
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy);
    public int fePerTick = CommonConfig.GENERAL.t2_fe_per_tick.get();

    public TileBoosterMechanical(BlockPos pos, BlockState state) {
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
        return CommonConfig.GENERAL.t2_fe_per_tick.get() * 100;
    }

    private CustomEnergyStorage createEnergyStorage() {
        return new CustomEnergyStorage(
                getMaxEnergy(),
                CommonConfig.GENERAL.t2_fe_per_tick.get() * 10
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
