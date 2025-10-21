package igentuman.blockbooster.setup;

import igentuman.blockbooster.block.BlockBoosterT1;
import igentuman.blockbooster.block.BlockBoosterT2;
import igentuman.blockbooster.block.BlockBoosterT3;
import igentuman.blockbooster.container.BoosterT1Container;
import igentuman.blockbooster.container.BoosterT2Container;
import igentuman.blockbooster.container.BoosterT3Container;
import igentuman.blockbooster.tile.TileBoosterT1;
import igentuman.blockbooster.tile.TileBoosterT2;
import igentuman.blockbooster.tile.TileBoosterT3;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static igentuman.blockbooster.BlockBooster.MODID;


public class Registration {

    // New NeoForge 1.21 style
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final List<Supplier<? extends ItemLike>> BOOSTER_TAB_ITEMS = new ArrayList<>();

    // Some common properties for our blocks and items
    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties();

    // Block registrations
    public static final DeferredBlock<BlockBoosterT1> BLOCKBOOSTER_T1 = BLOCKS.register("booster_t1", BlockBoosterT1::new);
    public static final DeferredBlock<BlockBoosterT2> BLOCKBOOSTER_T2 = BLOCKS.register("booster_t2", BlockBoosterT2::new);
    public static final DeferredBlock<BlockBoosterT3> BLOCKBOOSTER_T3 = BLOCKS.register("booster_t3", BlockBoosterT3::new);

    // Item registrations
    public static final DeferredItem<BlockItem> BLOCKBOOSTER_T1_ITEM = ITEMS.register("booster_t1", () -> new BlockItem(BLOCKBOOSTER_T1.get(), ITEM_PROPERTIES));
    public static final DeferredItem<BlockItem> BLOCKBOOSTER_T2_ITEM = ITEMS.register("booster_t2", () -> new BlockItem(BLOCKBOOSTER_T2.get(), ITEM_PROPERTIES));
    public static final DeferredItem<BlockItem> BLOCKBOOSTER_T3_ITEM = ITEMS.register("booster_t3", () -> new BlockItem(BLOCKBOOSTER_T3.get(), ITEM_PROPERTIES));

    // Creative tab - now defined after items
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BOOSTER_TAB = TABS.register("blockbooster",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.blockbooster"))
                    .icon(() -> BLOCKBOOSTER_T1_ITEM.get().getDefaultInstance())
                    .displayItems((displayParams, output) ->
                            BOOSTER_TAB_ITEMS.forEach(itemLike -> output.accept(itemLike.get())))
                    .withSearchBar()
                    .build()
    );

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        CONTAINERS.register(bus);
        TABS.register(bus);
    }

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileBoosterT1>> BLOCKBOOSTER_T1_BE = BLOCK_ENTITIES.register("booster_t1", () -> BlockEntityType.Builder.of(TileBoosterT1::new, BLOCKBOOSTER_T1.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileBoosterT2>> BLOCKBOOSTER_T2_BE = BLOCK_ENTITIES.register("booster_t2", () -> BlockEntityType.Builder.of(TileBoosterT2::new, BLOCKBOOSTER_T2.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileBoosterT3>> BLOCKBOOSTER_T3_BE = BLOCK_ENTITIES.register("booster_t3", () -> BlockEntityType.Builder.of(TileBoosterT3::new, BLOCKBOOSTER_T3.get()).build(null));

    public static final DeferredHolder<MenuType<?>, MenuType<BoosterT1Container>> BLOCKBOOSTER_T1_CONTAINER = CONTAINERS.register("booster_t1",
            Registration::createBoosterT1MenuType);
    public static final DeferredHolder<MenuType<?>, MenuType<BoosterT2Container>> BLOCKBOOSTER_T2_CONTAINER = CONTAINERS.register("booster_t2",
            Registration::createBoosterT2MenuType);
    public static final DeferredHolder<MenuType<?>, MenuType<BoosterT3Container>> BLOCKBOOSTER_T3_CONTAINER = CONTAINERS.register("booster_t3",
            Registration::createBoosterT3MenuType);
    
    // Menu type factory methods for NeoForge 1.21 compatibility
    private static MenuType<BoosterT1Container> createBoosterT1MenuType() {
        return IMenuTypeExtension.create(BoosterT1Container::new);
    }
    
    private static MenuType<BoosterT2Container> createBoosterT2MenuType() {
        return IMenuTypeExtension.create(BoosterT2Container::new);
    }
    
    private static MenuType<BoosterT3Container> createBoosterT3MenuType() {
        return IMenuTypeExtension.create(BoosterT3Container::new);
    }

    // Add items to creative tab after registration
    static {
        BOOSTER_TAB_ITEMS.add(BLOCKBOOSTER_T1_ITEM);
        BOOSTER_TAB_ITEMS.add(BLOCKBOOSTER_T2_ITEM);
        BOOSTER_TAB_ITEMS.add(BLOCKBOOSTER_T3_ITEM);
    }
}
