package igentuman.blockbooster;

import net.minecraftforge.common.config.Config;


@Config(modid = ModInfo.MODID)
public class ModConfig {
    public static BlockBoosterConfig boosterConfig = new BlockBoosterConfig();

    @Config.Name("debug info")
    @Config.Comment({
            "Show debug info, for development purposes only."
    })
    public static boolean DEBUG = false;

    public static class BlockBoosterConfig {
        @Config.Name("rf_per_tick")
        @Config.Comment({
                "RF per tick for booster to operate for each boost operation"
        })
        public int rf_per_tick = 10000;

        @Config.Name("boost_rate")
        @Config.Comment({
                "Boost rate (default 5)"
        })
        public int boost_rate = 5;

        @Config.Name("boost_on_top")
        @Config.Comment({
                "Boost entities on top of booster block?"
        })
        public boolean boost_on_top = true;

        @Config.Name("boost_on_bottom")
        @Config.Comment({
                "Boost entities on bottom of booster block?"
        })
        public boolean boost_on_bottom = true;

        @Config.Name("black_list")
        @Config.Comment({
                "Black list of tile entities",
                "format: some_mod:some_machine:1"
        })

        public String[] black_list = new String[] {

        };

        @Config.Name("white_list")
        @Config.Comment({
                "Same as black list, but allows boosting only machines in this list",
                "format: some_mod:some_machine:1"
        })

        public String[] white_list = new String[] {

        };

        @Config.Name("deactivate_with_redstone")
        @Config.Comment({
                "Use redstone to control booster?"
        })
        public boolean deactivate_with_redstone = true;
    }

}
