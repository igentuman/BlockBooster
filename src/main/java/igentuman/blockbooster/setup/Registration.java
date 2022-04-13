package igentuman.blockbooster.setup;

import igentuman.blockbooster.block.*;
import igentuman.blockbooster.tile.BoosterBE;
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
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, igentuman.blockbooster.BlockBooster.MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, igentuman.blockbooster.BlockBooster.MODID);
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

       
    public static final RegistryObject<BlockBooster> BLOCKBOOSTER = BLOCKS.register("blockbooster", BlockBooster::new);
    public static final RegistryObject<Item> BLOCKBOOSTER_ITEM = fromBlock(BLOCKBOOSTER);
    public static final RegistryObject<BlockEntityType<BoosterBE>> BLOCKBOOSTER_BE = BLOCK_ENTITIES.register("blockbooster", () -> BlockEntityType.Builder.of(BoosterBE::new, BLOCKBOOSTER.get()).build(null));
    public static final RegistryObject<MenuType<BoosterContainer>> BLOCKBOOSTER_CONTAINER = CONTAINERS.register("blockbooster",
            () -> IForgeMenuType.create((windowId, inv, data) -> new BoosterContainer(windowId, data.readBlockPos(), inv, inv.player)));


    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }
}
