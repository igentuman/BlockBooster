package igentuman.blockbooster.datagen;

import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModels extends ItemModelProvider {

    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, BlockBooster.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(Registration.BLOCKBOOSTER_ITEM.get().getRegistryName().getPath(), modLoc("block/blockbooster/main"));
    }
}
