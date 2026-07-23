package igentuman.blockbooster.datagen;

import igentuman.blockbooster.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

import static igentuman.blockbooster.BlockBooster.MODID;

public class BlockTags extends BlockTagsProvider {

    public BlockTags(DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(generator.getPackOutput(), lookupProvider, MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE)
                .add(
                        Registration.BLOCKBOOSTER_T1.get(),
                        Registration.BLOCKBOOSTER_T2.get(),
                        Registration.BLOCKBOOSTER_T3.get()
                        );

        tag(net.minecraft.tags.BlockTags.NEEDS_IRON_TOOL)
                .add(
                        Registration.BLOCKBOOSTER_T1.get(),
                        Registration.BLOCKBOOSTER_T2.get(),
                        Registration.BLOCKBOOSTER_T3.get()
                );
    }

    @Override
    public String getName() {
        return "Booster Tags";
    }

}
