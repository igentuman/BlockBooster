package igentuman.blockbooster.datagen;

import igentuman.blockbooster.BlockBooster;
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
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(true, new BbRecipes(generator, event.getLookupProvider()));
            generator.addProvider(event.includeServer(), new LootTableProvider(generator.getPackOutput(), Collections.emptySet(),
                    List.of(new LootTableProvider.SubProviderEntry(paramSet -> new BoosterLootTable(), LootContextParamSets.BLOCK)),
                    event.getLookupProvider()));
            BlockTags blockTags = new BlockTags(generator, event.getLookupProvider(), event.getExistingFileHelper());
            event.getGenerator().addProvider(
                    event.includeServer(),
                    blockTags
            );
        }


        if (event.includeClient()) {
            generator.addProvider(true, new BbLanguageProvider(generator, "en_us"));
        }
    }
}
