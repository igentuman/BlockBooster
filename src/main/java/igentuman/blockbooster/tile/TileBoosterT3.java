package igentuman.blockbooster.tile;

import igentuman.blockbooster.config.CommonConfig;
import igentuman.blockbooster.setup.Registration;
import igentuman.blockbooster.util.BoosterUtil;
import igentuman.blockbooster.util.CustomEnergyStorage;
import igentuman.blockbooster.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;

public class TileBoosterT3 extends AbstractBooster {

    public final CustomEnergyStorage energy = createEnergyStorage();
    public int fePerTick = CommonConfig.GENERAL.t3_fe_per_tick.get();

    public TileBoosterT3(BlockPos pos, BlockState state) {
        super(Registration.BLOCKBOOSTER_T3_BE.get(), pos, state);
    }

    @Override
    public void updateAttachedBlocks() {
        boolean changed = false;
        HashMap<Long, BlockEntity> newAttachedBlocks = new HashMap<>();
        int radius = CommonConfig.GENERAL.t3_scan_radius.get();

        // Scan in a cube area around the booster
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    // Skip the booster's own position
                    if (x == 0 && y == 0 && z == 0) continue;

                    BlockPos checkPos = getBlockPos().offset(x, y, z);
                    BlockEntity be = WorldUtil.getBlockEntity(checkPos, (ServerLevel) level);

                    if (be == null) continue;

                    // Check whitelist/blacklist
                    if (!getWhiteList().isEmpty()) {
                        if (!getWhiteList().contains(getBlockName(be))) {
                            continue;
                        }
                    } else if (getBlackList().contains(getBlockName(be))) {
                        continue;
                    }

                    long posKey = checkPos.asLong();
                    newAttachedBlocks.put(posKey, be);

                    // Preserve boost flag if this block was already tracked
                    if (!boostFlags.containsKey(posKey)) {
                        boostFlags.put(posKey, false);
                    }
                }
            }
        }

        // Check if anything changed
        if (newAttachedBlocks.size() != attachedBlocks.size()) {
            changed = true;
        } else {
            for (Long key : newAttachedBlocks.keySet()) {
                if (!attachedBlocks.containsKey(key) || !attachedBlocks.get(key).equals(newAttachedBlocks.get(key))) {
                    changed = true;
                    break;
                }
            }
        }

        if (changed) {
            attachedBlocks = newAttachedBlocks;
            for(Long key : attachedBlocks.keySet()) {
                if(!boostFlags.containsKey(key)) {
                    boostFlags.put(key, false);
                }
                if(!boostTimes.containsKey(key)) {
                    boostTimes.put(key, 0L);
                }
            }
            //clear out flags for removed blocks
            boostFlags.keySet().removeIf(key -> !attachedBlocks.containsKey(key));
            boostTimes.keySet().removeIf(key -> !attachedBlocks.containsKey(key));
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
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
        // Directly save energy value instead of using serializeNBT
        tag.putInt("Energy", energy.getEnergyStored());

        // Save boost flags
        ListTag flagsList = new ListTag();
        for (Long key : boostFlags.keySet()) {
            CompoundTag flagTag = new CompoundTag();
            flagTag.putLong("posKey", key);
            flagTag.putBoolean("enabled", boostFlags.get(key));
            flagsList.add(flagTag);
        }
        tag.put("boostFlags", flagsList);

        // Save boost times
        ListTag timesList = new ListTag();
        for (Long key : boostTimes.keySet()) {
            CompoundTag timeTag = new CompoundTag();
            timeTag.putLong("posKey", key);
            timeTag.putLong("time", boostTimes.get(key));
            timesList.add(timeTag);
        }
        tag.put("boostTimes", timesList);
    }

    @Override
    protected void loadBoosterData(CompoundTag tag) {
        // Directly load energy value instead of using deserializeNBT
        if (tag.contains("Energy")) {
            energy.setEnergy(tag.getInt("Energy"));
        }

        // Load boost flags
        if (tag.contains("boostFlags")) {
            boostFlags.clear();
            ListTag flagsList = tag.getList("boostFlags", Tag.TAG_COMPOUND);
            for (int i = 0; i < flagsList.size(); i++) {
                CompoundTag flagTag = flagsList.getCompound(i);
                long key = flagTag.contains("posKey") ? flagTag.getLong("posKey") : flagTag.getInt("index");
                boostFlags.put(key, flagTag.getBoolean("enabled"));
            }
        }

        // Load boost times
        if (tag.contains("boostTimes")) {
            boostTimes.clear();
            ListTag timesList = tag.getList("boostTimes", Tag.TAG_COMPOUND);
            for (int i = 0; i < timesList.size(); i++) {
                CompoundTag timeTag = timesList.getCompound(i);
                long key = timeTag.contains("posKey") ? timeTag.getLong("posKey") : timeTag.getInt("index");
                boostTimes.put(key, timeTag.getLong("time"));
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

    public boolean isIndexEnabled(long posKey) {
        return boostFlags.getOrDefault(posKey, false);
    }

    public void setIndexStatus(long posKey, boolean status) {
        boostFlags.put(posKey, status);
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public HashMap<Long, Long> getBoostTimes() {
        return boostTimes;
    }

    public boolean isSlowBlock(long posKey) {
        Long boostTime = boostTimes.get(posKey);
        if (boostTime == null) return false;
        return boostTime > CommonConfig.GENERAL.slow_block_threshold_ns.get();
    }
}