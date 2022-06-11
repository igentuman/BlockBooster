package igentuman.blockbooster.tile;

import com.sun.jna.platform.unix.solaris.LibKstat;
import igentuman.blockbooster.config.CommonConfig;
import igentuman.blockbooster.setup.Registration;
import igentuman.blockbooster.util.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class BoosterBE extends BlockEntity implements BlockEntityTicker {

    private final CustomEnergyStorage energy = createEnergyStorage();
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy);
    public BoosterBE(BlockPos pos, BlockState state) {
        super(Registration.BLOCKBOOSTER_BE.get(), pos, state);
    }
    private ArrayList<String> whiteList = CommonConfig.GENERAL.white_list.get();
    private ArrayList<String> blackList = CommonConfig.GENERAL.black_list.get();


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

    public String topBlock = "";
    public String bottomBlock = "";
    public String leftBlock = "";
    public String rightBlock = "";
    public String lastLeftBlock = "";
    public String lastTopBlock = "";
    public String lastBottomBlock = "";
    public String lastRightBlock = "";
    public boolean workingFlag = false;
    public int fePerTick = CommonConfig.GENERAL.fe_per_tick.get();
    public boolean[] sides = CommonConfig.GENERAL.getSides();

    protected String getRegistryLineForBE(BlockEntity be)
    {
        return BlockEntityType.getKey(be.getType()).toString();
    }

    public void tickServer() {

        if(level.hasNeighborSignal(worldPosition) && CommonConfig.GENERAL.deactivate_with_redstone.get()) {
            if(level.getBlockState(worldPosition).getValue(BlockStateProperties.POWERED) != false) {
                setChanged();
                level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(BlockStateProperties.POWERED, false),
                        Block.UPDATE_ALL);
            }
            return;
        }
        boolean changedFlag = false;
        boolean curWorkingFlag = false;
        for(int s = 0; s < 4; s++) {
            BlockPos pos = null;
            BlockEntity targetTE = null;
            switch (s) {
                case 0:
                    pos = worldPosition.above();
                    targetTE = level.getBlockEntity(pos);
                    topBlock = targetTE != null ? getRegistryLineForBE(targetTE): "";
                    changedFlag = lastLeftBlock.equals(topBlock) || changedFlag;
                    lastTopBlock = topBlock;
                    break;
                case 1:
                    pos = worldPosition.below();
                    targetTE = level.getBlockEntity(pos);
                    bottomBlock = targetTE != null ? getRegistryLineForBE(targetTE) : "";;
                    changedFlag = lastBottomBlock.equals(bottomBlock) || changedFlag;
                    lastBottomBlock = bottomBlock;
                    break;
                case 2:
                    pos = worldPosition.relative(Direction.WEST);
                    targetTE = level.getBlockEntity(pos);
                    leftBlock = targetTE != null ? getRegistryLineForBE(targetTE) : "";
                    changedFlag = lastLeftBlock.equals(leftBlock) || changedFlag;
                    lastLeftBlock = leftBlock;
                    break;
                case 3:
                    pos = worldPosition.relative(Direction.EAST);
                    targetTE = level.getBlockEntity(pos);
                    rightBlock = targetTE != null ? getRegistryLineForBE(targetTE) : "";
                    changedFlag = lastRightBlock.equals(rightBlock) || changedFlag;
                    lastRightBlock = rightBlock;
                    break;
            }
            //avoid infinite loop
            if(targetTE instanceof BoosterBE) return;
            if (targetTE != null && getEnergy() >= fePerTick && sides[s]) {
                //whitelist higher priority
                if(whiteList.size() > 0) {
                    if (!whiteList.contains(targetTE.getType().toString())) {
                        continue;
                    }
                } else if(blackList.contains(targetTE.getType().toString())) {
                    continue;
                }
                BlockEntityTicker<BlockEntity> ticker = targetTE.getBlockState()
                        .getTicker(level, (BlockEntityType<BlockEntity>) targetTE.getType());
                if (ticker != null) {
                    for (int i = 0; i < CommonConfig.GENERAL.boost_rate.get(); i++) {
                        ticker.tick(level, pos, targetTE.getBlockState(), targetTE);
                    }
                    consumeEnergy(fePerTick);
                    changedFlag = true;
                    curWorkingFlag = true;
                }
            } /*else if (targetTE != null && targetTE.getBlockState().isRandomlyTicking()) {
                if (level.random.nextInt(CommonConfig.GENERAL.boost_rate.get().intValue()) == 0) {
                    targetTE.getBlockState().randomTick((ServerLevel) level, pos, level.random);
                }sa
            }*/

            if(changedFlag) {
                setChanged();
                if (level.getBlockState(worldPosition).getValue(BlockStateProperties.POWERED) != curWorkingFlag) {
                    level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(BlockStateProperties.POWERED, curWorkingFlag),
                            Block.UPDATE_ALL);
                    workingFlag = curWorkingFlag;
                } else {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }

    private CustomEnergyStorage createEnergyStorage() {
        return new CustomEnergyStorage (
                CommonConfig.GENERAL.fe_per_tick.get()*16,
                        CommonConfig.GENERAL.fe_per_tick.get()*4
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
        tag.putString("topBlock",topBlock);
        tag.putString("bottomBlock",bottomBlock);
        tag.putString("leftBlock",leftBlock);
        tag.putString("rightBlock",rightBlock);
    }

    private void loadClientData(CompoundTag tag) {
        if (tag.contains("Energy")) {
            energy.deserializeNBT(tag.get("Energy"));
        }
        topBlock = tag.getString("topBlock");
        bottomBlock = tag.getString("bottomBlock");
        leftBlock = tag.getString("leftBlock");
        rightBlock = tag.getString("rightBlock");
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("Energy")) {
            energy.deserializeNBT(tag.get("Energy"));
        }
        topBlock = tag.getString("topBlock");
        bottomBlock = tag.getString("bottomBlock");
        leftBlock = tag.getString("leftBlock");
        rightBlock = tag.getString("rightBlock");
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
       tag.put("Energy", energy.serializeNBT());
        tag.putString("topBlock",topBlock);
        tag.putString("bottomBlock",bottomBlock);
        tag.putString("leftBlock",leftBlock);
        tag.putString("rightBlock",rightBlock);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void tick(Level world, BlockPos p_155254_, BlockState p_155255_, BlockEntity p_155256_) {
        if(!world.isClientSide()) {
            //tickServer();
        }
    }
}
