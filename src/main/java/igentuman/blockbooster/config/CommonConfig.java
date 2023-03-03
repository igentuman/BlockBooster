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
        public final ForgeConfigSpec.ConfigValue<Integer> t1_fe_per_tick;
        public final ForgeConfigSpec.ConfigValue<Integer> t1_boost_rate;

        public final ForgeConfigSpec.ConfigValue<Integer> t2_fe_per_tick;
        public final ForgeConfigSpec.ConfigValue<Integer> t2_boost_rate;

        public final ForgeConfigSpec.ConfigValue<Integer> mana_per_tick;
        public final ForgeConfigSpec.ConfigValue<Integer> mana_booster_rate;

        public final ForgeConfigSpec.ConfigValue<Boolean> deactivate_with_redstone;

        public final ForgeConfigSpec.ConfigValue<ArrayList> white_list;
        public final ForgeConfigSpec.ConfigValue<ArrayList> black_list;


        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            t1_fe_per_tick = builder
                    .comment("Booster Tier 1 FE per tick to operate")
                    .define("t1_fe_per_tick", 5000);
            t1_boost_rate = builder
                    .comment("Booster Tier 1 boost rate")
                    .define("t1_boost_rate", 2);
            t2_fe_per_tick = builder
                    .comment("Booster Tier 2 FE per tick to operate")
                    .define("t2_fe_per_tick", 10000);
            t2_boost_rate = builder
                    .comment("Booster Tier 2 boost rate")
                    .define("t2_boost_rate", 5);
            mana_per_tick = builder
                    .comment("Mana Booster mana per tick to operate")
                    .define("mana_per_tick", 1000);
            mana_booster_rate = builder
                    .comment("Mana Booster boost rate")
                    .define("mana_booster_rate", 5);
            deactivate_with_redstone = builder
                    .comment("Deactivate booster with redstone signal")
                    .define("deactivate_with_redstone", true);
            black_list = builder
                    .comment("Blacklist of block entities (example: \"minecraft:furnace\",\"somemod:machine\")")
                    .define("black_list", new ArrayList());
            white_list = builder
                    .comment("Whitelist of block entities (example: \"minecraft:furnace\",\"somemod:machine\") has higher priority")
                    .define("white_list", new ArrayList());
            builder.pop();
        }
    }
}