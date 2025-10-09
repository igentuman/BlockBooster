package igentuman.blockbooster.tile;

import igentuman.blockbooster.config.CommonConfig;
import igentuman.blockbooster.util.BoosterUtil;
import igentuman.blockbooster.util.TPSTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;

public abstract class AbstractBooster extends BlockEntity implements BlockEntityTicker, ITileBooster {

    protected LinkedList<String> whiteList = loadWhiteList();
    protected LinkedList<String> blackList = loadBlackList();
    protected HashMap<Integer, BlockEntity> attachedBlocks = new HashMap<>();
    protected HashMap<Integer, Long> boostTimes = new HashMap<>();
    protected HashMap<Integer, Boolean> boostFlags = new HashMap<>();
    protected byte[] boostFlag = new byte[]{0, 0, 0, 0, 0, 0};
    public boolean isDisabled = false;
    public boolean isLagging = false;
    protected long tick = 0;

    public AbstractBooster(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    private LinkedList<String> loadBlackList() {
        LinkedList<String> list = new LinkedList<>();
        for (var s : CommonConfig.GENERAL.black_list.get()) {
            list.add((String) s);
        }
        return list;
    }

    private LinkedList<String> loadWhiteList() {
        LinkedList<String> list = new LinkedList<>();
        for (var s : CommonConfig.GENERAL.white_list.get()) {
            list.add((String) s);
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
            
            BlockEntity be = level.getBlockEntity(new BlockPos(getBlockPos().relative(direction, 1)));
            boolean contains = attachedBlocks.containsKey(direction.ordinal());
            
            if (be == null) {
                if (contains) {
                    changed = true;
                    attachedBlocks.remove(direction.ordinal());
                }
                continue;
            }
            
            if (contains && attachedBlocks.get(direction.ordinal()).equals(be)) continue;
            
            if (!whiteList.isEmpty()) {
                if (!whiteList.contains(getBlockName(be))) {
                    continue;
                }
            } else if (blackList.contains(getBlockName(be))) {
                continue;
            }
            
            if (contains) {
                attachedBlocks.remove(direction.ordinal());
            }
            changed = true;
            attachedBlocks.put(direction.ordinal(), be);
        }
        
        if (changed) {
            boostFlags.clear();
            boostTimes.clear();
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
        if (level == null) return;
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
        for (Integer id : attachedBlocks.keySet()) {
            if (id > boostFlag.length - 1) break;
            if (boostFlag[id] == 0) continue;
            
            BlockEntity be = attachedBlocks.get(id);
            if (be == null || !canBoost()) return;
            
            if (BoosterUtil.BoostBlockEntity(level, be.getBlockPos(), be, getBoostRate())) {
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
        if(tick % 10 == 0) {
            updateAttachedBlocks();
        }
    }

    @Override
    public void tick(Level world, BlockPos pos, BlockState state, BlockEntity be) {
    }

    public HashMap<Integer, BlockEntity> getAttachedBlocks() {
        return attachedBlocks;
    }

    public byte[] getBoostFlag() {
        return boostFlag;
    }

    public boolean isIndexEnabled(int i) {
        return boostFlag[i] == 1;
    }

    @Override
    public void setIndexStatus(int i, byte status) {
        boostFlag[i] = status;
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
        tag.putByteArray("boostFlag", boostFlag);
        saveBoosterData(tag);
    }

    protected void loadClientData(CompoundTag tag) {
        isDisabled = tag.getBoolean("isDisabled");
        isLagging = tag.getBoolean("isLagging");
        if (tag.getByteArray("boostFlag").length == boostFlag.length) {
            boostFlag = tag.getByteArray("boostFlag");
        }
        loadBoosterData(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        isDisabled = tag.getBoolean("isDisabled");
        isLagging = tag.getBoolean("isLagging");
        if (tag.getByteArray("boostFlag").length == boostFlag.length) {
            boostFlag = tag.getByteArray("boostFlag");
        }
        loadBoosterData(tag);
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putBoolean("isDisabled", isDisabled);
        tag.putBoolean("isLagging", isLagging);
        tag.putByteArray("boostFlag", boostFlag);
        saveBoosterData(tag);
    }

    public boolean isLagging() {
        return isLagging;
    }

    public double getCurrentTPS() {
        return TPSTracker.getCurrentTPS();
    }
}
