package igentuman.blockbooster.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class CommonConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();

    private static boolean loaded = false;
    private static List<Runnable> loadActions = new ArrayList<>();

    public static void setLoaded() {
        if (!loaded)
            loadActions.forEach(Runnable::run);
        loaded = true;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static void onLoad(Runnable action) {
        if (loaded)
            action.run();
        else
            loadActions.add(action);
    }

    public static class General {
        public final ForgeConfigSpec.ConfigValue<Integer> fe_per_tick;
        public final ForgeConfigSpec.ConfigValue<Integer> boost_rate;

        public final ForgeConfigSpec.ConfigValue<Boolean> boost_top;
        public final ForgeConfigSpec.ConfigValue<Boolean> boost_bottom;
      /*public final ForgeConfigSpec.ConfigValue<Boolean> boost_left;
        public final ForgeConfigSpec.ConfigValue<Boolean> boost_right;*/

      public final ForgeConfigSpec.ConfigValue<ArrayList> white_list;
      public final ForgeConfigSpec.ConfigValue<ArrayList> black_list;


        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            fe_per_tick = builder
                    .comment("Forge Energy per tick to operate")
                    .define("fe_per_tick", 10000);
            boost_rate = builder
                    .comment("Boost rate")
                    .define("boost_rate", 5);
            boost_top = builder
                    .comment("Boost entities at the top of booster block?")
                    .define("boost_top", true);
            boost_bottom = builder
                    .comment("Boost entities at the bottom of booster block?")
                    .define("boost_bottom", true);
           /* boost_left = builder
                    .comment("Boost entities on left of booster block?")
                    .define("boost_left", true);
            boost_right = builder
                    .comment("Boost entities on right of booster block?")
                    .define("boost_right", true);*/
            black_list = builder
                    .comment("Blacklist of block entities (example: \"minecraft:furnace\",\"somemod:machine\")")
                    .define("black_list", new ArrayList());
            white_list = builder
                    .comment("Whitelist of block entities (example: \"minecraft:furnace\",\"somemod:machine\") has higher priority")
                    .define("white_list", new ArrayList());
            builder.pop();
        }

        public boolean[] getSides()
        {
            boolean[] sides = new boolean[4];
            sides[0] = boost_top.get();
            sides[1] = boost_bottom.get();
            sides[2] = false;
            sides[3] = false;
            return sides;
        }
    }
}