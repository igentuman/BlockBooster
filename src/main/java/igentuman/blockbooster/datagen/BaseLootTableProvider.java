package igentuman.blockbooster.datagen;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class BaseLootTableProvider implements LootTableSubProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    
    // NeoForge 1.21: CopyNbtFunction and SetContainerContents removed from loot system
    // Using simplified version that just drops the block
    public static LootTable.Builder createStandardTable(String name, Block block, BlockEntityType<?> type) {
        // TODO: Update with proper NBT copying once loot API stabilizes
        return createSimpleTable(name, block);
    }

    public static LootTable.Builder createSimpleTable(String name, Block block) {
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(block));
        return LootTable.lootTable().withPool(builder);
    }

    public static LootTable.Builder createSilkTouchTable(String name, Block block, Item lootItem, float min, float max) {
        // NeoForge 1.21: Enchantment handling simplified
        // TODO: Reimplement with proper enchantment registry access when API stabilizes
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantValue.exactly(1))
                .add(AlternativesEntry.alternatives(
                                LootItem.lootTableItem(block),
                                LootItem.lootTableItem(lootItem)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                                        .apply(ApplyExplosionDecay.explosionDecay())
                        )
                );
        return LootTable.lootTable().withPool(builder);
    }
}