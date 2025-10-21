package igentuman.blockbooster.datagen;

import igentuman.blockbooster.setup.Registration;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class BbRecipes extends RecipeProvider {

    // NeoForge 1.21: RecipeProvider constructor requires HolderLookup.Provider
    public BbRecipes(DataGenerator generatorIn, java.util.concurrent.CompletableFuture<net.minecraft.core.HolderLookup.Provider> lookupProvider) {
        super(generatorIn.getPackOutput(), lookupProvider);
    }
    
    // Keep old constructor for compatibility
    public BbRecipes(DataGenerator generatorIn) {
        this(generatorIn, java.util.concurrent.CompletableFuture.completedFuture(null));
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.BLOCKBOOSTER_T1.get())
                .pattern("m#m")
                .pattern("xcx")
                .pattern("#x#")
                .define('x', Tags.Items.DUSTS_REDSTONE)
                .define('#', Tags.Items.INGOTS_IRON)
                .define('m', Tags.Items.GEMS_EMERALD)
                .define('c', Items.CLOCK)
                .group("blockbooster")
                .unlockedBy("mysterious", InventoryChangeTrigger.TriggerInstance.hasItems(Items.EMERALD))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.BLOCKBOOSTER_T2.get())
                .pattern("m#m")
                .pattern("xcx")
                .pattern("#x#")
                .define('x', Tags.Items.DUSTS_REDSTONE)
                .define('#', Tags.Items.INGOTS_GOLD)
                .define('m', Tags.Items.GEMS_EMERALD)
                .define('c', Registration.BLOCKBOOSTER_T1.get())
                .group("blockbooster")
                .unlockedBy("mysterious", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BLOCKBOOSTER_T1.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.BLOCKBOOSTER_T3.get())
                .pattern("m#m")
                .pattern("xcx")
                .pattern("#x#")
                .define('x', Tags.Items.DUSTS_REDSTONE)
                .define('#', Tags.Items.GEMS_DIAMOND)
                .define('m', Tags.Items.GEMS_EMERALD)
                .define('c', Registration.BLOCKBOOSTER_T2.get())
                .group("blockbooster")
                .unlockedBy("mysterious", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BLOCKBOOSTER_T2.get()))
                .save(consumer);
    }
}
