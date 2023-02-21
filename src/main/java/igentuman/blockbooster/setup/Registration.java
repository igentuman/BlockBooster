package igentuman.blockbooster.setup;

import igentuman.blockbooster.block.*;
import igentuman.blockbooster.container.BoosterT1Container;
import igentuman.blockbooster.container.BoosterT2Container;
import igentuman.blockbooster.tile.TileBoosterT1;
import igentuman.blockbooster.tile.TileBoosterT2;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registration {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, igentuman.blockbooster.BlockBooster.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, igentuman.blockbooster.BlockBooster.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, igentuman.blockbooster.BlockBooster.MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, igentuman.blockbooster.BlockBooster.MODID);
    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        CONTAINERS.register(bus);
    }

    // Some common properties for our blocks and items
    public static final BlockBehaviour.Properties BLOCK_PROPERTIES = BlockBehaviour.Properties.of(Material.STONE).strength(2f).requiresCorrectToolForDrops();
    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(ModSetup.ITEM_GROUP);

       
    public static final RegistryObject<BlockBoosterT1> BLOCKBOOSTER_T1 = BLOCKS.register("booster_t1", BlockBoosterT1::new);
    public static final RegistryObject<BlockBoosterT2> BLOCKBOOSTER_T2 = BLOCKS.register("booster_t2", BlockBoosterT2::new);

    public static final RegistryObject<Item> BLOCKBOOSTER_T1_ITEM = fromBlock(BLOCKBOOSTER_T1);
    public static final RegistryObject<Item> BLOCKBOOSTER_T2_ITEM = fromBlock(BLOCKBOOSTER_T2);

    public static final RegistryObject<BlockEntityType<TileBoosterT1>> BLOCKBOOSTER_T1_BE = BLOCK_ENTITIES.register("booster_t1", () -> BlockEntityType.Builder.of(TileBoosterT1::new, BLOCKBOOSTER_T1.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileBoosterT2>> BLOCKBOOSTER_T2_BE = BLOCK_ENTITIES.register("booster_t2", () -> BlockEntityType.Builder.of(TileBoosterT2::new, BLOCKBOOSTER_T2.get()).build(null));

    public static final RegistryObject<MenuType<BoosterT1Container>> BLOCKBOOSTER_T1_CONTAINER = CONTAINERS.register("booster_t1",
            () -> IForgeMenuType.create((windowId, inv, data) -> new BoosterT1Container(windowId, data.readBlockPos(), inv, inv.player)));
    public static final RegistryObject<MenuType<BoosterT2Container>> BLOCKBOOSTER_T2_CONTAINER = CONTAINERS.register("booster_t2",
            () -> IForgeMenuType.create((windowId, inv, data) -> new BoosterT2Container(windowId, data.readBlockPos(), inv, inv.player)));

    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }
}
