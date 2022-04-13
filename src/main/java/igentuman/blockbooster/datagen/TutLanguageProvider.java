package igentuman.blockbooster.datagen;

import igentuman.blockbooster.setup.Registration;
import igentuman.blockbooster.block.BlockBooster;
import igentuman.blockbooster.setup.ModSetup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class TutLanguageProvider extends LanguageProvider {

    public TutLanguageProvider(DataGenerator gen, String locale) {
        super(gen, igentuman.blockbooster.BlockBooster.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + ModSetup.TAB_NAME, "Block Booster");
        add(BlockBooster.MESSAGE_BLOCKBOOSTER, "Icreases tick rate for the block entity on the top of it. Requires energy");
        add(BlockBooster.SCREEN_TUTORIAL_BLOCKBOOSTER, "Block Booster");

        add(Registration.BLOCKBOOSTER.get(), "Block Booster");
       
    }
}
