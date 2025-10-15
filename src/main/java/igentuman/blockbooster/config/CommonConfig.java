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
        public final ForgeConfigSpec.ConfigValue<Integer> boosters_per_chunk;

        public final ForgeConfigSpec.ConfigValue<Integer> t1_fe_per_tick;
        public final ForgeConfigSpec.ConfigValue<Integer> t1_boost_rate;

        public final ForgeConfigSpec.ConfigValue<Integer> t2_fe_per_tick;
        public final ForgeConfigSpec.ConfigValue<Integer> t2_boost_rate;

        public final ForgeConfigSpec.ConfigValue<Integer> t3_fe_per_tick;
        public final ForgeConfigSpec.ConfigValue<Integer> t3_boost_rate;
        public final ForgeConfigSpec.ConfigValue<Integer> t3_scan_radius;

        public final ForgeConfigSpec.ConfigValue<Integer> mana_per_tick;
        public final ForgeConfigSpec.ConfigValue<Integer> mana_booster_rate;

        public final ForgeConfigSpec.ConfigValue<Boolean> deactivate_with_redstone;

        public final ForgeConfigSpec.ConfigValue<List<String>> white_list;
        public final ForgeConfigSpec.ConfigValue<List<String>> black_list;

        public final ForgeConfigSpec.ConfigValue<Boolean> enable_tps_protection;
        public final ForgeConfigSpec.ConfigValue<Double> min_tps_threshold;

        public final ForgeConfigSpec.ConfigValue<Boolean> prevent_slow_blocks;
        public final ForgeConfigSpec.ConfigValue<Long> slow_block_threshold_ns;

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            boosters_per_chunk = builder
                    .comment("Limit boosters per chunk. Boosters might affect server performance negatively. Consider limiting boosters per chunk")
                    .defineInRange("boosters_per_chunk", 5, 1, 20);
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
            t3_fe_per_tick = builder
                    .comment("Booster Tier 3 FE per tick to operate")
                    .define("t3_fe_per_tick", 20000);
            t3_boost_rate = builder
                    .comment("Booster Tier 3 boost rate")
                    .define("t3_boost_rate", 10);
            t3_scan_radius = builder
                    .comment("Booster Tier 3 scan radius (scans a cube area)")
                    .defineInRange("t3_scan_radius", 3, 1, 10);
            mana_per_tick = builder
                    .comment("Mana Booster mana per tick to operate")
                    .define("mana_per_tick", 100);
            mana_booster_rate = builder
                    .comment("Mana Booster boost rate")
                    .define("mana_booster_rate", 5);
            deactivate_with_redstone = builder
                    .comment("Deactivate booster with redstone signal")
                    .define("deactivate_with_redstone", true);
            black_list = builder
                    .comment("Blacklist of block entities by block id or block tags (example: \"minecraft:furnace\",\"#somemod:some_tag\")")
                    .define("black_list", new ArrayList<>(List.of("#mekanism:cardboard_blacklist")));
            white_list = builder
                    .comment("Whitelist of block entities by id or block tags (example: \"minecraft:furnace\",\"#somemod:some_tag\") has higher priority")
                    .define("white_list", new ArrayList<>());
            enable_tps_protection = builder
                    .comment("Enable TPS-based lag protection. Boosters will stop working when server TPS drops below threshold")
                    .define("enable_tps_protection", true);
            min_tps_threshold = builder
                    .comment("Minimum TPS threshold for boosters to operate. If server TPS drops below this value, boosters will pause")
                    .defineInRange("min_tps_threshold", 15.0, 1.0, 20.0);
            prevent_slow_blocks = builder
                    .comment("Prevent boosting of slow blocks that take longer than the threshold to process")
                    .define("prevent_slow_blocks", true);
            slow_block_threshold_ns = builder
                    .comment("Threshold in nanoseconds for considering a block as 'slow'. Blocks taking longer will be marked as slow (1ms = 1000000ns)")
                    .defineInRange("slow_block_threshold_ns", 5000000L, 100000L, 100000000L);
            builder.pop();
        }
    }
}