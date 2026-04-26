package igentuman.blockbooster.tile;

import igentuman.blockbooster.config.CommonConfig;
import igentuman.blockbooster.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
import java.util.Optional;

public class TileBoosterMana extends AbstractBooster implements ManaPool {

    private int mana;
    private boolean addedToManaNetwork = false;
    public int manaPerTick = CommonConfig.GENERAL.mana_per_tick.get();

    public TileBoosterMana(BlockPos pos, BlockState state) {
        super(Registration.BLOCKBOOSTER_MANA_BE.get(), pos, state);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        BotaniaAPI.instance().getManaNetworkInstance().fireManaNetworkEvent(this, ManaBlockType.POOL, ManaNetworkAction.REMOVE);
        addedToManaNetwork = false;
    }

    @Override
    public void tickServer() {
        addToManaNetwork();
        super.tickServer();
    }

    @Override
    protected boolean canBoost() {
        return mana >= manaPerTick;
    }

    @Override
    protected int getBoostRate() {
        return CommonConfig.GENERAL.mana_booster_rate.get();
    }

    @Override
    protected long consumeResource() {
        consumeMana();
        return manaPerTick;
    }

    @Override
    public String getResourceUnit() {
        return "Mana";
    }

    @Override
    protected void saveBoosterData(CompoundTag tag) {
        tag.putInt("mana", mana);
        
        // Save boost flags for direction-based indexing
        CompoundTag flagsTag = new CompoundTag();
        for (Long key : boostFlags.keySet()) {
            flagsTag.putBoolean(String.valueOf(key), boostFlags.get(key));
        }
        tag.put("boostFlags", flagsTag);
    }

    @Override
    protected void loadBoosterData(CompoundTag tag) {
        mana = tag.getInt("mana");
        
        // Load boost flags
        if (tag.contains("boostFlags")) {
            boostFlags.clear();
            CompoundTag flagsTag = tag.getCompound("boostFlags");
            for (String key : flagsTag.getAllKeys()) {
                boostFlags.put(Long.parseLong(key), flagsTag.getBoolean(key));
            }
        }
    }

    private void addToManaNetwork() {
        if (!ManaNetworkHandler.instance.isPoolIn(level, this) && !isRemoved()) {
            BotaniaAPI.instance().getManaNetworkInstance().fireManaNetworkEvent(this, ManaBlockType.POOL, ManaNetworkAction.ADD);
            addedToManaNetwork = true;
        }
    }

    private void consumeMana() {
        mana -= manaPerTick;
        setChanged(level, getBlockPos(), getBlockState());
    }

    // ManaPool implementation
    @Override
    public boolean isOutputtingPower() {
        return false;
    }

    public int getMaxMana() {
        return CommonConfig.GENERAL.mana_per_tick.get() * 100;
    }

    @Override
    public Optional<DyeColor> getColor() {
        return Optional.empty();
    }

    @Override
    public void setColor(Optional<DyeColor> color) {
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
        return mana >= getMaxMana();
    }

    @Override
    public void receiveMana(int mana) {
        this.mana += mana;
        setChanged(level, getBlockPos(), getBlockState());
    }

    @Override
    public boolean canReceiveManaFromBursts() {
        return true;
    }

    public int getMana() {
        return mana;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == BotaniaForgeCapabilities.MANA_RECEIVER) {
            return (LazyOptional<T>) LazyOptional.of(() -> this);
        }
        return super.getCapability(cap, side);
    }
}
