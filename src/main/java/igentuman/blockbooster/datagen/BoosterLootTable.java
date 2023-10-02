package igentuman.blockbooster.datagen;

import igentuman.blockbooster.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

import static igentuman.blockbooster.setup.Registration.*;

public class BoosterLootTable implements LootTableSubProvider {

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> builder) {
        builder.accept(BLOCKBOOSTER_T1.getId(),
                BaseLootTableProvider.createSimpleTable("booster_t1", BLOCKBOOSTER_T1.get()));

        builder.accept(BLOCKBOOSTER_T2.getId(),
                BaseLootTableProvider.createSimpleTable("booster_t2", BLOCKBOOSTER_T2.get()));

        builder.accept(BLOCKBOOSTER_MANA.getId(),
                BaseLootTableProvider.createSimpleTable("booster_mana", BLOCKBOOSTER_MANA.get()));

        //lootTables.put(Registration.BLOCKBOOSTER_TIME.get(), createStandardTable("booster_time", Registration.BLOCKBOOSTER_TIME.get()));
        //lootTables.put(Registration.BLOCKBOOSTER_MECHANICAL.get(), createStandardTable("booster_mechanical", Registration.BLOCKBOOSTER_MECHANICAL.get()));
    }


}
