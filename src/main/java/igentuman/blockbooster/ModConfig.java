package igentuman.blockbooster;

import net.minecraftforge.common.config.Config;


@Config(modid = ModInfo.MODID)
public class ModConfig {
    public static BlockBoosterTier1Config boosterT1Config = new BlockBoosterTier1Config();
    public static BlockBoosterTier2Config boosterT2Config = new BlockBoosterTier2Config();
    public static GeneralConfig general = new GeneralConfig();

    public static class GeneralConfig {
        @Config.Name("black_list")
        @Config.Comment({
                "Black list of tile entities",
                "use command /booster_show_block_id to find out block id",
                "format: some_mod:some_machine:registry_id:meta"
        })

        public String[] black_list = new String[] {

        };

        @Config.Name("white_list")
        @Config.Comment({
                "Same as black list, but allows boosting only machines in this list",
                "use command /booster_show_block_id to find out block id",
                "format: some_mod:some_machine:registry_id:meta"
        })

        public String[] white_list = new String[] {

        };

        @Config.Name("deactivate_with_redstone")
        @Config.Comment({
                "Use redstone to control booster?"
        })
        public boolean deactivate_with_redstone = true;
    }

    public static class BlockBoosterTier1Config {
        @Config.Name("rf_per_tick")
        @Config.Comment({
                "RF per tick for booster to operate for each boost operation"
        })
        public int rf_per_tick = 10000;

        @Config.Name("boost_rate")
        @Config.Comment({
                "Boost rate (default 2)"
        })
        public int boost_rate = 2;
    }

    public static class BlockBoosterTier2Config {
        @Config.Name("rf_per_tick")
        @Config.Comment({
                "RF per tick for booster to operate for each boost operation"
        })
        public int rf_per_tick = 20000;

        @Config.Name("boost_rate")
        @Config.Comment({
                "Boost rate (default 5)"
        })
        public int boost_rate = 5;
    }

}
