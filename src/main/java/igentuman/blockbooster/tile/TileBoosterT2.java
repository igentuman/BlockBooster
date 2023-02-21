package igentuman.blockbooster.tile;

import igentuman.blockbooster.config.CommonConfig;
import igentuman.blockbooster.setup.Registration;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

public class TileBoosterT2 extends BlockEntity implements BlockEntityTicker, ITileBooster {

    private final CustomEnergyStorage energy = createEnergyStorage();
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy);

    private ArrayList<String> whiteList = CommonConfig.GENERAL.white_list.get();
    private ArrayList<String> blackList = CommonConfig.GENERAL.black_list.get();

    private HashMap<Integer, BlockEntity> attachedBlocks = new HashMap<>();

    public byte[] getBoostFlag() {
        return boostFlag;
    }

    private byte[] boostFlag = new byte[] {0,0,0,0,0,0};

    public TileBoosterT2(BlockPos pos, BlockState state) {
        super(Registration.BLOCKBOOSTER_T2_BE.get(), pos, state);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    public int getEnergy()
    {
        return energy.getEnergyStored();
    }

    public void consumeEnergy(int amount)
    {
        energy.consumeEnergy(amount);
    }

    public boolean isDisabled = false;
    public int fePerTick = CommonConfig.GENERAL.t2_fe_per_tick.get();
    private long tick = 0;

    protected String getRegistryLineForBE(BlockEntity be)
    {
        return BlockEntityType.getKey(be.getType()).toString();
    }


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
            if(be == null || getEnergy() < fePerTick) return;
            BlockEntityTicker<BlockEntity> ticker = be.getBlockState()
                    .getTicker(level, (BlockEntityType<BlockEntity>) be.getType());
            if (ticker != null) {
                for (int i = 0; i < CommonConfig.GENERAL.t2_boost_rate.get(); i++) {
                    ticker.tick(level, be.getBlockPos(), be.getBlockState(), be);
                }
                consumeEnergy(fePerTick);
            }
        }
    }

    private CustomEnergyStorage createEnergyStorage() {
        return new CustomEnergyStorage (
                getMaxEnergy(),
                        CommonConfig.GENERAL.t2_fe_per_tick.get()*10
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
        tag.put("Energy", energy.serializeNBT());
        tag.putBoolean("isDisabled", isDisabled);
        tag.putByteArray("boostFlag", boostFlag);
    }

    private void loadClientData(CompoundTag tag) {
        if (tag.contains("Energy")) {
            energy.deserializeNBT(tag.get("Energy"));
        }
        isDisabled = tag.getBoolean("isDisabled");
        if(tag.getByteArray("boostFlag").length == 6) {
            boostFlag = tag.getByteArray("boostFlag");
        }
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("Energy")) {
            energy.deserializeNBT(tag.get("Energy"));
        }
        isDisabled = tag.getBoolean("isDisabled");
        if(tag.getByteArray("boostFlag").length == 6) {
            boostFlag = tag.getByteArray("boostFlag");
        }
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
       tag.put("Energy", energy.serializeNBT());
        tag.putBoolean("isDisabled", isDisabled);
        tag.putByteArray("boostFlag", boostFlag);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void tick(Level world, BlockPos pos, BlockState state, BlockEntity be) {
    }

    public HashMap<Integer, BlockEntity> getAttachedBlocks() {
        return attachedBlocks;
    }

    public int getMaxEnergy() {
        return CommonConfig.GENERAL.t2_fe_per_tick.get()*100;
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

    public void tickClient() {
        updateAttachedBlocks();
    }
}
