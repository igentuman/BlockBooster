package igentuman.blockbooster.datagen;

import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static igentuman.blockbooster.BlockBooster.MODID;

public class BlockTags extends BlockTagsProvider {

    public BlockTags(DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider,ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), lookupProvider, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE)
                .add(
                        Registration.BLOCKBOOSTER_T1.get(),
                        Registration.BLOCKBOOSTER_T2.get(),
                        Registration.BLOCKBOOSTER_MANA.get()
                        );

        tag(net.minecraft.tags.BlockTags.NEEDS_IRON_TOOL)
                .add(
                        Registration.BLOCKBOOSTER_T1.get(),
                        Registration.BLOCKBOOSTER_T2.get(),
                        Registration.BLOCKBOOSTER_MANA.get()
                );
    }

    @Override
    public String getName() {
        return "Booster Tags";
    }

}
