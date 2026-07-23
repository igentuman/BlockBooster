package igentuman.blockbooster.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Collections;
import java.util.List;

import static igentuman.blockbooster.BlockBooster.MODID;

@EventBusSubscriber(modid = MODID)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherServerData(GatherDataEvent.Server event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(true, new BbRecipes.Runner(generator.getPackOutput(), event.getLookupProvider()));
        generator.addProvider(true, new LootTableProvider(generator.getPackOutput(), Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(paramSet -> new BoosterLootTable(), LootContextParamSets.BLOCK)),
                event.getLookupProvider()));
        generator.addProvider(true, new BlockTags(generator, event.getLookupProvider()));
    }

    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(true, new BbLanguageProvider(generator, "en_us"));
    }
}
