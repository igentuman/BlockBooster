package igentuman.blockbooster.tile;

import igentuman.blockbooster.config.CommonConfig;
import igentuman.blockbooster.setup.Registration;
import igentuman.blockbooster.util.BoosterUtil;
import igentuman.blockbooster.util.CustomEnergyStorage;
import igentuman.blockbooster.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;

public class TileBoosterT3 extends AbstractBooster {

    public final CustomEnergyStorage energy = createEnergyStorage();
    public int fePerTick = CommonConfig.GENERAL.t3_fe_per_tick.get();

    public TileBoosterT3(BlockPos pos, BlockState state) {
        super(igentuman.blockbooster.setup.Registration.BLOCKBOOSTER_T3_BE.get(), pos, state);
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
    protected void saveBoosterData(ValueOutput output) {
        output.putInt("Energy", energy.getEnergyStored());

        ValueOutput.ValueOutputList flagsList = output.childrenList("boostFlags");
        for (Long key : boostFlags.keySet()) {
            ValueOutput flagOut = flagsList.addChild();
            flagOut.putLong("posKey", key);
            flagOut.putBoolean("enabled", boostFlags.get(key));
        }

        ValueOutput.ValueOutputList timesList = output.childrenList("boostTimes");
        for (Long key : boostTimes.keySet()) {
            ValueOutput timeOut = timesList.addChild();
            timeOut.putLong("posKey", key);
            timeOut.putLong("time", boostTimes.get(key));
        }
    }

    @Override
    protected void loadBoosterData(ValueInput input) {
        energy.setEnergy(input.getIntOr("Energy", 0));

        boostFlags.clear();
        for (ValueInput flagIn : input.childrenListOrEmpty("boostFlags")) {
            long key = flagIn.getLongOr("posKey", 0L);
            boostFlags.put(key, flagIn.getBooleanOr("enabled", false));
        }

        boostTimes.clear();
        for (ValueInput timeIn : input.childrenListOrEmpty("boostTimes")) {
            long key = timeIn.getLongOr("posKey", 0L);
            boostTimes.put(key, timeIn.getLongOr("time", 0L));
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