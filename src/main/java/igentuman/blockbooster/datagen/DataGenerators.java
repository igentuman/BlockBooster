package igentuman.blockbooster.datagen;

import igentuman.blockbooster.BlockBooster;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.List;

import static igentuman.blockbooster.BlockBooster.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(true, new BbRecipes(generator));
            generator.addProvider(event.includeServer(), new LootTableProvider(generator.getPackOutput(), Collections.emptySet(),
                    List.of(new LootTableProvider.SubProviderEntry(BoosterLootTable::new, LootContextParamSets.BLOCK))));
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
