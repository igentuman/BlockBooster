package igentuman.blockbooster.tile;

import igentuman.blockbooster.config.CommonConfig;
import igentuman.blockbooster.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.mana.ManaBlockType;
import vazkii.botania.api.mana.ManaNetworkAction;
import vazkii.botania.api.mana.ManaPool;
import vazkii.botania.common.handler.ManaNetworkHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class TileBoosterMana extends BlockEntity implements BlockEntityTicker, ITileBooster, ManaPool {


    private ArrayList<String> whiteList = CommonConfig.GENERAL.white_list.get();
    private ArrayList<String> blackList = CommonConfig.GENERAL.black_list.get();

    private HashMap<Integer, BlockEntity> attachedBlocks = new HashMap<>();
    private int mana;

    private boolean addedToManaNetwork = false;

    public byte[] getBoostFlag() {
        return boostFlag;
    }

    private byte[] boostFlag = new byte[] {0,0,0,0,0,0};

    public TileBoosterMana(BlockPos pos, BlockState state) {
        super(Registration.BLOCKBOOSTER_MANA_BE.get(), pos, state);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        BotaniaAPI.instance().getManaNetworkInstance().fireManaNetworkEvent(this, ManaBlockType.POOL, ManaNetworkAction.REMOVE);
        addedToManaNetwork = false;
    }

    public boolean isDisabled = false;
    public int manaPerTick = CommonConfig.GENERAL.mana_per_tick.get();
    private long tick = 0;

    private void updateRedstoneControl()
    {
        if(level.hasNeighborSignal(worldPosition) && CommonConfig.GENERAL.deactivate_with_redstone.get()) {
            if(level.getBlockState(worldPosition).getValue(BlockStateProperties.POWERED) != false) {
                setChanged();
                level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(BlockStateProperties.POWERED, false),
                        Block.UPDATE_ALL);
            }
            isDisabled = true;
            return;
        }
        isDisabled = false;
    }

    public String getBlockName(BlockEntity be)
    {
        return Registry.BLOCK.getKey(be.getBlockState().getBlock()).toString();
    }

    public void updateAttachedBlocks()
    {
        boolean changed = false;
        for(Direction direction: Direction.values()) {
            BlockEntity be = level.getBlockEntity(new BlockPos(getBlockPos().relative(direction, 1)));
            boolean contains = attachedBlocks.containsKey(direction.ordinal());
            if(be == null) {
                if(contains) {
                    changed = true;
                    attachedBlocks.remove(direction.ordinal());
                }
                continue;
            }
            if(contains && attachedBlocks.get(direction.ordinal()).equals(be)) continue;
            if (whiteList.size() > 0) {
                if (!whiteList.contains(getBlockName(be))) {
                    continue;
                }
            } else if (blackList.contains(getBlockName(be))) {
                continue;
            }
            if(contains) {
                attachedBlocks.remove(direction.ordinal());
            }
            changed = true;
            attachedBlocks.put(direction.ordinal(), be);
        }
        if(changed) {
            setChanged(level, getBlockPos(), getBlockState());
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public void tickServer() {
        if(level == null) return;
        addToManaNetwork();
        tick++;
        if(tick % 10 == 0) {
            if(!level.isClientSide()) {
                boolean lastState = isDisabled;
                updateRedstoneControl();
                if(lastState != isDisabled) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                }
                if(isDisabled) {
                    return;
                }
            }
            updateAttachedBlocks();
        }

        for(Integer id: attachedBlocks.keySet()) {
            if(id > boostFlag.length-1) break;
            if(boostFlag[id] == 0) continue;
            BlockEntity be = attachedBlocks.get(id);
            if(be == null || mana < manaPerTick) return;
            BlockEntityTicker<BlockEntity> ticker = be.getBlockState()
                    .getTicker(level, (BlockEntityType<BlockEntity>) be.getType());
            if (ticker != null) {
                for (int i = 0; i < CommonConfig.GENERAL.mana_booster_rate.get(); i++) {
                    ticker.tick(level, be.getBlockPos(), be.getBlockState(), be);
                }
                consumeMana();
            }
        }
    }

    private void addToManaNetwork() {
        //if(addedToManaNetwork) return;
        if (!ManaNetworkHandler.instance.isPoolIn(level, this) && !isRemoved()) {
            BotaniaAPI.instance().getManaNetworkInstance().fireManaNetworkEvent(this, ManaBlockType.POOL, ManaNetworkAction.ADD);
            addedToManaNetwork = true;
        }
    }

    private void consumeMana() {
        mana-=manaPerTick;
        setChanged(level, getBlockPos(), getBlockState());
    }

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

    private void saveClientData(CompoundTag tag) {
        tag.putInt("mana", mana);
        tag.putBoolean("isDisabled", isDisabled);
        tag.putByteArray("boostFlag", boostFlag);
    }

    private void loadClientData(CompoundTag tag) {
        mana = tag.getInt("mana");
        isDisabled = tag.getBoolean("isDisabled");
        if(tag.getByteArray("boostFlag").length == 6) {
            boostFlag = tag.getByteArray("boostFlag");
        }
    }

    @Override
    public void load(CompoundTag tag) {
        mana = tag.getInt("mana");
        isDisabled = tag.getBoolean("isDisabled");
        if(tag.getByteArray("boostFlag").length == 6) {
            boostFlag = tag.getByteArray("boostFlag");
        }
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putInt("mana", mana);
        tag.putBoolean("isDisabled", isDisabled);
        tag.putByteArray("boostFlag", boostFlag);
    }

    @Override
    public void tick(Level world, BlockPos pos, BlockState state, BlockEntity be) {
    }

    public HashMap<Integer, BlockEntity> getAttachedBlocks() {
        return attachedBlocks;
    }

    @Override
    public boolean isOutputtingPower() {
        return false;
    }

    public int getMaxMana() {
        return CommonConfig.GENERAL.mana_per_tick.get()*100;
    }

    @Override
    public Optional<DyeColor> getColor() {
        return Optional.empty();
    }

    @Override
    public void setColor(Optional<DyeColor> color) {

    }

    public boolean isIndexEnabled(int i) {
        return boostFlag[i] == 1;
    }

    public void setIndexStatus(int i, byte status) {
        boostFlag[i] = status;
        setChanged(level, getBlockPos(), getBlockState());
        level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(BlockStateProperties.POWERED, false),
                Block.UPDATE_ALL);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == BotaniaForgeCapabilities.MANA_RECEIVER) {
            return (LazyOptional<T>) LazyOptional.of(() -> this);
        }
        return super.getCapability(cap, side);
    }
    
    public void tickClient() {
        updateAttachedBlocks();
    }

    @Override
    public Level getManaReceiverLevel() {
        return getLevel();
    }

    @Override
    public BlockPos getManaReceiverPos() {
        return getBlockPos();
    }

    @Override
    public int getCurrentMana() {
        return mana;
    }

    @Override
    public boolean isFull() {
        return mana>=getMaxMana();
    }

    @Override
    public void receiveMana(int mana) {
        this.mana+=mana;
        setChanged(level, getBlockPos(), getBlockState());
    }

    @Override
    public boolean canReceiveManaFromBursts() {
        return true;
    }

    public int getMana() {
        return mana;
    }
}
