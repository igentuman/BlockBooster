package igentuman.blockbooster.datagen;

import igentuman.blockbooster.setup.Registration;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class BbRecipes extends RecipeProvider {

    public BbRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        HolderGetter<Item> items = this.registries.lookupOrThrow(Registries.ITEM);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, Registration.BLOCKBOOSTER_T1.get())
                .pattern("m#m")
                .pattern("xcx")
                .pattern("#x#")
                .define('x', Tags.Items.DUSTS_REDSTONE)
                .define('#', Tags.Items.INGOTS_IRON)
                .define('m', Tags.Items.GEMS_EMERALD)
                .define('c', Items.CLOCK)
                .group("blockbooster")
                .unlockedBy("mysterious", InventoryChangeTrigger.TriggerInstance.hasItems(Items.EMERALD))
                .save(this.output);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, Registration.BLOCKBOOSTER_T2.get())
                .pattern("m#m")
                .pattern("xcx")
                .pattern("#x#")
                .define('x', Tags.Items.DUSTS_REDSTONE)
                .define('#', Tags.Items.INGOTS_GOLD)
                .define('m', Tags.Items.GEMS_EMERALD)
                .define('c', Registration.BLOCKBOOSTER_T1.get())
                .group("blockbooster")
                .unlockedBy("mysterious", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BLOCKBOOSTER_T1.get()))
                .save(this.output);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, Registration.BLOCKBOOSTER_T3.get())
                .pattern("m#m")
                .pattern("xcx")
                .pattern("#x#")
                .define('x', Tags.Items.DUSTS_REDSTONE)
                .define('#', Tags.Items.GEMS_DIAMOND)
                .define('m', Tags.Items.GEMS_EMERALD)
                .define('c', Registration.BLOCKBOOSTER_T2.get())
                .group("blockbooster")
                .unlockedBy("mysterious", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BLOCKBOOSTER_T2.get()))
                .save(this.output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new BbRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "BlockBooster Recipes";
        }
    }
}
