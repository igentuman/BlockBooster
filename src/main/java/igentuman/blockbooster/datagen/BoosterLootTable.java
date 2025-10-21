package igentuman.blockbooster.datagen;

import igentuman.blockbooster.setup.Registration;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

import static igentuman.blockbooster.setup.Registration.*;

public class BoosterLootTable implements LootTableSubProvider {

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> builder) {
        // NeoForge 1.21: Use ResourceKey instead of ResourceLocation
        builder.accept(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("blockbooster", "blocks/booster_t1")),
                BaseLootTableProvider.createSimpleTable("booster_t1", BLOCKBOOSTER_T1.get()));

        builder.accept(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("blockbooster", "blocks/booster_t2")),
                BaseLootTableProvider.createSimpleTable("booster_t2", BLOCKBOOSTER_T2.get()));

        builder.accept(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("blockbooster", "blocks/booster_t3")),
                BaseLootTableProvider.createSimpleTable("booster_t3", BLOCKBOOSTER_T3.get()));

    }

}
