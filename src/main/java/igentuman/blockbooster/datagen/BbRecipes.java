package igentuman.blockbooster.datagen;

import igentuman.blockbooster.setup.Registration;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class BbRecipes extends RecipeProvider {

    public BbRecipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(Registration.BLOCKBOOSTER_T1.get())
                .pattern("mmm")
                .pattern("x#x")
                .pattern("#x#")
                .define('x', Tags.Items.DUSTS_REDSTONE)
                .define('#', Tags.Items.INGOTS_IRON)
                .define('m', Tags.Items.GEMS_EMERALD)
                .group("blockbooster")
                .unlockedBy("mysterious", InventoryChangeTrigger.TriggerInstance.hasItems(Items.EMERALD))
                .save(consumer);
        ShapedRecipeBuilder.shaped(Registration.BLOCKBOOSTER_T2.get())
                .pattern("mmm")
                .pattern("x#x")
                .pattern("#x#")
                .define('x', Tags.Items.DUSTS_REDSTONE)
                .define('#', Tags.Items.INGOTS_GOLD)
                .define('m', Tags.Items.GEMS_EMERALD)
                .group("blockbooster")
                .unlockedBy("mysterious", InventoryChangeTrigger.TriggerInstance.hasItems(Items.EMERALD))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Registration.BLOCKBOOSTER_MANA.get())
                .pattern("mmm")
                .pattern("x#x")
                .pattern("#x#")
                .define('x', Tags.Items.DUSTS_REDSTONE)
                .define('#', Tags.Items.INGOTS_NETHERITE)
                .define('m', Tags.Items.GEMS_EMERALD)
                .group("blockbooster")
                .unlockedBy("mysterious", InventoryChangeTrigger.TriggerInstance.hasItems(Items.EMERALD))
                .save(consumer);
    }
}
