package igentuman.blockbooster.datagen;

import igentuman.blockbooster.BlockBooster;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = BlockBooster.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(true, new TutRecipes(generator));
            BlockTags blockTags = new BlockTags(generator, event.getExistingFileHelper());
            generator.addProvider(true, blockTags);
        }
        if (event.includeClient()) {
            generator.addProvider(true, new BlockStates(generator, event.getExistingFileHelper()));
            generator.addProvider(true, new ItemModels(generator, event.getExistingFileHelper()));
            generator.addProvider(true, new TutLanguageProvider(generator, "en_us"));
        }
    }
}
