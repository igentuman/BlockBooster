package igentuman.blockbooster.tile;

import igentuman.blockbooster.config.CommonConfig;
import igentuman.blockbooster.util.BoosterUtil;
import igentuman.blockbooster.util.TPSTracker;
import igentuman.blockbooster.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static igentuman.blockbooster.util.BoosterUtil.getBlocksByTagKey;

public abstract class AbstractBooster extends BlockEntity implements BlockEntityTicker, ITileBooster {

    protected LinkedList<String> whiteList;
    protected LinkedList<String> blackList;
    protected HashMap<Long, BlockEntity> attachedBlocks = new HashMap<>();
    protected HashMap<Long, Long> boostTimes = new HashMap<>();
    protected HashMap<Long, Boolean> boostFlags = new HashMap<>();
    public boolean isDisabled = false;
    public boolean isLagging = false;
    protected long tick = 0;
    protected long lastHeartbeat = 0;
    public boolean preventSlowBlocks = CommonConfig.GENERAL.prevent_slow_blocks.get();
    public long slowBlockThreshold = CommonConfig.GENERAL.slow_block_threshold_ns.get();

    public AbstractBooster(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    /**
     * Lazy initialization of whitelist - loads from config on first access
     */
    protected LinkedList<String> getWhiteList() {
        if (whiteList == null) {
            whiteList = loadList(CommonConfig.GENERAL.white_list.get());
        }
        return whiteList;
    }

    /**
     * Lazy initialization of blacklist - loads from config on first access
     */
    protected LinkedList<String> getBlackList() {
        if (blackList == null) {
            blackList = loadList(CommonConfig.GENERAL.black_list.get());
        }
        return blackList;
    }

    /**
     * Load a list of block IDs from config, expanding tags to individual block IDs
     * @param configList The list from config containing block IDs and/or tags (prefixed with #)
     * @return A LinkedList of expanded block IDs
     */
    private LinkedList<String> loadList(List<? extends String> configList) {
        LinkedList<String> list = new LinkedList<>();
        for (String entry : configList) {
            if (entry.contains("#")) {
                // This is a tag reference, expand it to individual blocks
                try {
                    String tagKey = entry.replace("#", "");
                    HashSet<Block> blocks = getBlocksByTagKey(tagKey);
                    if (blocks != null && !blocks.isEmpty()) {
                        list.addAll(blocks.stream()
                                .map(b -> ForgeRegistries.BLOCKS.getKey(b).toString())
                                .toList());
                    }
                } catch (Exception e) {
                    // Log error but continue processing other entries
                    System.err.println("BlockBooster: Failed to load tag '" + entry + "': " + e.getMessage());
                }
            } else {
                // Direct block ID reference
                list.add(entry);
            }
        }
        return list;
    }

    /**
     * Get the name of a block entity for whitelist/blacklist checking
     */
    public String getBlockName(BlockEntity be) {
        return ForgeRegistries.BLOCKS.getKey(be.getBlockState().getBlock()).toString();
    }

    /**
     * Update redstone control state
     */
    protected void updateRedstoneControl() {
        if (level.hasNeighborSignal(worldPosition) && CommonConfig.GENERAL.deactivate_with_redstone.get()) {
            if (level.getBlockState(worldPosition).getValue(BlockStateProperties.POWERED) != false) {
                setChanged();
                level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(BlockStateProperties.POWERED, false),
                        Block.UPDATE_ALL);
            }
            isDisabled = true;
            return;
        }
        isDisabled = false;
    }

    /**
     * Update the list of attached blocks that can be boosted
     * Can be overridden for custom scanning logic (e.g., T3 area scan)
     */
    public void updateAttachedBlocks() {
        boolean changed = false;
        for (Direction direction : Direction.values()) {
            if (shouldSkipDirection(direction)) continue;
            
            BlockPos checkPos = getBlockPos().relative(direction, 1);
            long posKey = checkPos.asLong();
            BlockEntity be = WorldUtil.getBlockEntity(checkPos, (ServerLevel) level);
            if (be == null || be instanceof AbstractBooster) continue;
            // Check whitelist/blacklist
            if (!getWhiteList().isEmpty()) {
                if (!getWhiteList().contains(getBlockName(be))) {
                    continue;
                }
            } else if (getBlackList().contains(getBlockName(be))) {
                continue;
            }
            boolean contains = attachedBlocks.containsKey(posKey);
            
            if (be == null) {
                if (contains) {
                    changed = true;
                    attachedBlocks.remove(posKey);
                }
                continue;
            }
            
            // Skip if the block entity is another booster
            if (be instanceof AbstractBooster) {
                if (contains) {
                    changed = true;
                    attachedBlocks.remove(posKey);
                }
                continue;
            }
            
            if (contains && attachedBlocks.get(posKey).equals(be)) continue;
            
            if (!getWhiteList().isEmpty()) {
                if (!getWhiteList().contains(getBlockName(be))) {
                    continue;
                }
            } else if (getBlackList().contains(getBlockName(be))) {
                continue;
            }
            
            if (contains) {
                attachedBlocks.remove(posKey);
            }
            changed = true;
            attachedBlocks.put(posKey, be);
        }
        
        if (changed) {
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

    /**
     * Override this to limit which directions are scanned (e.g., T1 only scans up/down)
     */
    protected boolean shouldSkipDirection(Direction direction) {
        return false;
    }

    /**
     * Check if TPS is acceptable for boosting
     */
    protected boolean checkTPS() {
        if (!CommonConfig.GENERAL.enable_tps_protection.get()) {
            return true;
        }
        return TPSTracker.isTpsAcceptable(CommonConfig.GENERAL.min_tps_threshold.get());
    }

    /**
     * Main server tick logic
     */
    @Override
    public void tickServer() {
        if (level == null || level.getGameTime() == lastHeartbeat) return;
        lastHeartbeat = level.getGameTime();
        tick++;
        
        if (tick % 20 == 0) {
            if (!level.isClientSide()) {
                boolean lastState = isDisabled;
                boolean lastLagState = isLagging;
                updateRedstoneControl();
                
                // Check TPS
                isLagging = !checkTPS();
                
                if (lastState != isDisabled || lastLagState != isLagging) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                }
                if (isDisabled || isLagging) {
                    return;
                }
            }
            updateAttachedBlocks();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }

        // Don't boost if lagging
        if (isLagging) {
            return;
        }

        processBoostingLogic();
    }

    /**
     * Process the actual boosting of attached blocks
     * Override this for custom boosting logic
     */
    protected void processBoostingLogic() {
        for (Long posKey : attachedBlocks.keySet()) {
            if (!boostFlags.getOrDefault(posKey, false)) continue;
            BlockEntity be = attachedBlocks.get(posKey);
            if (be == null || be.isRemoved() || !canBoost()) continue;

            // Check if slow block prevention is enabled and this block is slow
            if (preventSlowBlocks) {
                Long lastBoostTime = boostTimes.get(posKey);
                if (lastBoostTime != null && lastBoostTime > slowBlockThreshold) {
                    assert level != null;
                    if (level.getGameTime() % 10 != 0) {
                        continue; // Skip boosting this slow block
                    }
                }
            }

            BoosterUtil.BoostResult result = BoosterUtil.BoostBlockEntityWithTiming(level, be.getBlockPos(), be, getBoostRate());
            if (result.success) {
                boostTimes.put(posKey, result.timeNanos);
                consumeResource();
            }
        }
    }
    /**
     * Check if the booster has enough resources to boost
     */
    protected abstract boolean canBoost();

    /**
     * Get the boost rate for this booster
     */
    protected abstract int getBoostRate();

    /**
     * Consume the resource needed for boosting (energy, mana, etc.)
     */
    protected abstract void consumeResource();

    /**
     * Save booster-specific data to NBT
     */
    protected abstract void saveBoosterData(CompoundTag tag);

    /**
     * Load booster-specific data from NBT
     */
    protected abstract void loadBoosterData(CompoundTag tag);

    @Override
    public void tickClient() {
        for (Long key : attachedBlocks.keySet()) {
            BlockEntity be = attachedBlocks.get(key);
            if (be == null) {
                attachedBlocks.put(key, level.getBlockEntity(BlockPos.of(key)));
            }
        }
    }

    @Override
    public void tick(Level world, BlockPos pos, BlockState state, BlockEntity be) {
    }

    public HashMap<Long, BlockEntity> getAttachedBlocks() {
        return attachedBlocks;
    }

    public HashMap<Long, Boolean> getBoostFlags() {
        return boostFlags;
    }

    public boolean isIndexEnabled(long i) {
        return boostFlags.getOrDefault(i, false);
    }

    @Override
    public void setIndexStatus(long posKey, boolean status) {
        boostFlags.put(posKey, status);
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    // NBT Serialization
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveClientData(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag != null) {
            loadClientData(tag);
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        handleUpdateTag(tag);
    }

    protected void saveClientData(CompoundTag tag) {
        tag.putBoolean("isDisabled", isDisabled);
        tag.putBoolean("isLagging", isLagging);
        saveBoosterData(tag);
    }

    protected void loadClientData(CompoundTag tag) {
        isDisabled = tag.getBoolean("isDisabled");
        isLagging = tag.getBoolean("isLagging");
        loadBoosterData(tag);
        if(attachedBlocks.size() != boostFlags.size()) {
            attachedBlocks.clear();
            for(Long key: boostFlags.keySet()) {
                attachedBlocks.put(key, level.getBlockEntity(BlockPos.of(key)));
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        isDisabled = tag.getBoolean("isDisabled");
        isLagging = tag.getBoolean("isLagging");
        loadBoosterData(tag);
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putBoolean("isDisabled", isDisabled);
        tag.putBoolean("isLagging", isLagging);
        saveBoosterData(tag);
    }

    public boolean isLagging() {
        return isLagging;
    }

    public double getCurrentTPS() {
        return TPSTracker.getCurrentTPS();
    }
}
