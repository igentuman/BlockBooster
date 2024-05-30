package igentuman.blockbooster.datagen;

import igentuman.blockbooster.setup.Registration;
import igentuman.blockbooster.block.BlockBoosterT1;
import igentuman.blockbooster.setup.ModSetup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class BbLanguageProvider extends LanguageProvider {

    public BbLanguageProvider(DataGenerator gen, String locale) {
        super(gen.getPackOutput(), igentuman.blockbooster.BlockBooster.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + ModSetup.TAB_NAME, "Block Booster");
        add("gui.block_booster", "Block Booster");
        add("gui.creative", "Creative Player can see block ids");
        add("gui.checkbox.boost", "Boost");
        add("gui.energy.info", "Energy: %s/%s FE");
        add("gui.mana.info", "Mana: %s/%s");
        add("gui.block_booster.disabled", "Disabled");
        add("hint.booster_t1", "Boosts only Top/Bottom blocks. FE per boost: %s. Boost rate: x%s");
        add("hint.booster_t2", "FE per boost: %s. Boost rate: x%s");
        add("hint.booster_mana", "Uses mana to work. Mana per boost: %s. Boost rate: x%s");
        add("booster.limit_message", "Boosters per chunk limit reached. Only %s per chunk.");

        add(Registration.BLOCKBOOSTER_T1.get(), "Block Booster Tier 1");
        add(Registration.BLOCKBOOSTER_T2.get(), "Block Booster Tier 2");
        add(Registration.BLOCKBOOSTER_MANA.get(), "Mana Block Booster");
    }
}
