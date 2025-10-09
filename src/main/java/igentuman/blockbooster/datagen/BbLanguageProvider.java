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
        add("gui.block_booster_t3", "Block Booster Tier 3");
        add("gui.creative", "Creative Player can see block ids");
        add("gui.checkbox.boost", "Boost");
        add("gui.energy.info", "Energy: %s/%s FE");
        add("gui.mana.info", "Mana: %s/%s");
        add("gui.block_booster.disabled", "Disabled");
        add("gui.tps.ok", "§aServer TPS: OK");
        add("gui.tps.lagging", "§cServer TPS: Low");
        add("gui.tps.current", "Current TPS: %s");
        add("gui.tps.boosting_paused", "§7Boosting paused due to lag");
        add("gui.boost.time", "§7Boost time: %s ms");
        add("gui.boost.slow_block", "§cSlow block - not boosting");
        add("hint.booster_t1", "Boosts only Top/Bottom blocks. \nFE per block: %s. \nBoost rate: x%s");
        add("hint.booster_t2", "Boosts up to 6 blocks around. \nFE per block: %s. Boost rate: x%s");
        add("hint.booster_t3", "Boosts blocks around in radius of %s blocks. \nFE per block: %s. \nBoost rate: x%s");
        add("hint.booster_mana", "Uses mana to work. \nMana per block: %s. \nBoost rate: x%s");
        add("booster.limit_message", "Boosters per chunk limit reached. Only %s per chunk.");

        add(Registration.BLOCKBOOSTER_T1.get(), "Block Booster Tier 1");
        add(Registration.BLOCKBOOSTER_T2.get(), "Block Booster Tier 2");
        add(Registration.BLOCKBOOSTER_T3.get(), "Block Booster Tier 3");
        add(Registration.BLOCKBOOSTER_MANA.get(), "Mana Block Booster");
    }
}
