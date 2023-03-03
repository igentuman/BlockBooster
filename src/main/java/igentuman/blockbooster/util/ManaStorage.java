package igentuman.blockbooster.util;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import vazkii.botania.api.mana.ManaReceiver;

public class ManaStorage implements ManaReceiver {


    @Override
    public Level getManaReceiverLevel() {
        return null;
    }

    @Override
    public BlockPos getManaReceiverPos() {
        return null;
    }

    @Override
    public int getCurrentMana() {
        return 0;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public void receiveMana(int mana) {

    }

    @Override
    public boolean canReceiveManaFromBursts() {
        return false;
    }
}