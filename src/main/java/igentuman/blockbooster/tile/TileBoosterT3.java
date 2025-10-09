package igentuman.blockbooster.tile;

import igentuman.blockbooster.config.CommonConfig;
import igentuman.blockbooster.setup.Registration;
import igentuman.blockbooster.util.BoosterUtil;
import igentuman.blockbooster.util.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

public class TileBoosterT3 extends AbstractBooster {

    private final CustomEnergyStorage energy = createEnergyStorage();
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy);
    public int fePerTick = CommonConfig.GENERAL.t3_fe_per_tick.get();

    public TileBoosterT3(BlockPos pos, BlockState state) {
        super(Registration.BLOCKBOOSTER_T3_BE.get(), pos, state);
    }

    @Override
    public void updateAttachedBlocks() {
        boolean changed = false;
        HashMap<Integer, BlockEntity> newAttachedBlocks = new HashMap<>();
        int radius = CommonConfig.GENERAL.t3_scan_radius.get();
        int index = 0;

        // Scan in a cube area around the booster
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    // Skip the booster's own position
                    if (x == 0 && y == 0 && z == 0) continue;

                    BlockPos checkPos = getBlockPos().offset(x, y, z);
                    BlockEntity be = level.getBlockEntity(checkPos);

                    if (be == null) continue;

                    // Check whitelist/blacklist
                    if (whiteList.size() > 0) {
                        if (!whiteList.contains(getBlockName(be))) {
                            continue;
                        }
                    } else if (blackList.contains(getBlockName(be))) {
                        continue;
                    }

                    newAttachedBlocks.put(index, be);

                    // Preserve boost flag if this block was already tracked
                    if (!boostFlags.containsKey(index)) {
                        boostFlags.put(index, false);
                    }

                    index++;
                }
            }
        }

        // Check if anything changed
        if (newAttachedBlocks.size() != attachedBlocks.size()) {
            changed = true;
        } else {
            for (Integer key : newAttachedBlocks.keySet()) {
                if (!attachedBlocks.containsKey(key) || !attachedBlocks.get(key).equals(newAttachedBlocks.get(key))) {
                    changed = true;
                    break;
                }
            }
        }

        if (changed) {
            attachedBlocks = newAttachedBlocks;
            boostFlags.clear();
            boostTimes.clear();
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void processBoostingLogic() {
        for (Integer id : attachedBlocks.keySet()) {
            if (!boostFlags.getOrDefault(id, false)) continue;
            BlockEntity be = attachedBlocks.get(id);
            if (be == null || be.isRemoved() || !canBoost()) continue;

            // Check if slow block prevention is enabled and this block is slow
            if (CommonConfig.GENERAL.prevent_slow_blocks.get()) {
                Long lastBoostTime = boostTimes.get(id);
                if (lastBoostTime != null && lastBoostTime > CommonConfig.GENERAL.slow_block_threshold_ns.get()) {
                    continue; // Skip boosting this slow block
                }
            }

            BoosterUtil.BoostResult result = BoosterUtil.BoostBlockEntityWithTiming(level, be.getBlockPos(), be, getBoostRate());
            if (result.success) {
                boostTimes.put(id, result.timeNanos);
                consumeResource();
            }
        }
    }

    @Override
    protected boolean canBoost() {
        return getEnergy() >= fePerTick;
    }

    @Override
    protected int getBoostRate() {
        return CommonConfig.GENERAL.t3_boost_rate.get();
    }

    @Override
    protected void consumeResource() {
        consumeEnergy(fePerTick);
    }

    @Override
    protected void saveBoosterData(CompoundTag tag) {
        tag.put("Energy", energy.serializeNBT());

        // Save boost flags
        ListTag flagsList = new ListTag();
        for (Integer key : boostFlags.keySet()) {
            CompoundTag flagTag = new CompoundTag();
            flagTag.putInt("index", key);
            flagTag.putBoolean("enabled", boostFlags.get(key));
            flagsList.add(flagTag);
        }
        tag.put("boostFlags", flagsList);

        // Save boost times
        ListTag timesList = new ListTag();
        for (Integer key : boostTimes.keySet()) {
            CompoundTag timeTag = new CompoundTag();
            timeTag.putInt("index", key);
            timeTag.putLong("time", boostTimes.get(key));
            timesList.add(timeTag);
        }
        tag.put("boostTimes", timesList);
    }

    @Override
    protected void loadBoosterData(CompoundTag tag) {
        if (tag.contains("Energy")) {
            energy.deserializeNBT(tag.get("Energy"));
        }

        // Load boost flags
        if (tag.contains("boostFlags")) {
            boostFlags.clear();
            ListTag flagsList = tag.getList("boostFlags", Tag.TAG_COMPOUND);
            for (int i = 0; i < flagsList.size(); i++) {
                CompoundTag flagTag = flagsList.getCompound(i);
                boostFlags.put(flagTag.getInt("index"), flagTag.getBoolean("enabled"));
            }
        }

        // Load boost times
        if (tag.contains("boostTimes")) {
            boostTimes.clear();
            ListTag timesList = tag.getList("boostTimes", Tag.TAG_COMPOUND);
            for (int i = 0; i < timesList.size(); i++) {
                CompoundTag timeTag = timesList.getCompound(i);
                boostTimes.put(timeTag.getInt("index"), timeTag.getLong("time"));
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
        return CommonConfig.GENERAL.t3_fe_per_tick.get() * 100;
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

    @Override
    public boolean isIndexEnabled(int i) {
        return boostFlags.getOrDefault(i, false);
    }

    @Override
    public void setIndexStatus(int i, byte status) {
        boostFlags.put(i, status == 1);
        setChanged(level, getBlockPos(), getBlockState());
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    public HashMap<Integer, Boolean> getBoostFlags() {
        return boostFlags;
    }

    public HashMap<Integer, Long> getBoostTimes() {
        return boostTimes;
    }

    public boolean isSlowBlock(int index) {
        Long boostTime = boostTimes.get(index);
        if (boostTime == null) return false;
        return boostTime > CommonConfig.GENERAL.slow_block_threshold_ns.get();
    }
}