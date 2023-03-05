package igentuman.blockbooster.datagen;

import igentuman.blockbooster.setup.Registration;
import net.minecraft.data.DataGenerator;

public class BoosterLootTable extends BaseLootTableProvider  {

    public BoosterLootTable(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void addTables() {
        lootTables.put(Registration.BLOCKBOOSTER_T1.get(), createStandardTable("booster_t1", Registration.BLOCKBOOSTER_T1.get()));
        lootTables.put(Registration.BLOCKBOOSTER_T2.get(), createStandardTable("booster_t2", Registration.BLOCKBOOSTER_T2.get()));
        lootTables.put(Registration.BLOCKBOOSTER_MANA.get(), createStandardTable("booster_mana", Registration.BLOCKBOOSTER_MANA.get()));
        //lootTables.put(Registration.BLOCKBOOSTER_TIME.get(), createStandardTable("booster_time", Registration.BLOCKBOOSTER_TIME.get()));
        //lootTables.put(Registration.BLOCKBOOSTER_MECHANICAL.get(), createStandardTable("booster_mechanical", Registration.BLOCKBOOSTER_MECHANICAL.get()));
    }


}
