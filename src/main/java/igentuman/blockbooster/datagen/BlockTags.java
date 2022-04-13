package igentuman.blockbooster.datagen;

import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockTags extends BlockTagsProvider {

    public BlockTags(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, BlockBooster.MODID, helper);
    }

    @Override
    protected void addTags() {
        tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE)
                .add(Registration.BLOCKBOOSTER.get());
        tag(net.minecraft.tags.BlockTags.NEEDS_IRON_TOOL)
                .add(Registration.BLOCKBOOSTER.get());
    }

    @Override
    public String getName() {
        return "Booster Tags";
    }
}
