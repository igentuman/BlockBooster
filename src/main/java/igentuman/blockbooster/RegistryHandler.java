package igentuman.blockbooster;

import igentuman.blockbooster.block.BlockBlockBoosterT1;
import igentuman.blockbooster.block.BlockBlockBoosterT2;
import igentuman.blockbooster.tile.TileBlockBoosterT1;
import igentuman.blockbooster.tile.TileBlockBoosterT2;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber
public class RegistryHandler {

    @ObjectHolder("blockbooster:booster_t1")
    public static Block BLOCK_BOOSTER_T1 = new BlockBlockBoosterT1();

    @ObjectHolder("blockbooster:booster_t2")
    public static Block BLOCK_BOOSTER_T2 = new BlockBlockBoosterT2();

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(BLOCK_BOOSTER_T1);
        event.getRegistry().register(BLOCK_BOOSTER_T2);

        GameRegistry.registerTileEntity(
                TileBlockBoosterT1.class,
                BLOCK_BOOSTER_T1.getRegistryName()
        );
        GameRegistry.registerTileEntity(
                TileBlockBoosterT2.class,
                BLOCK_BOOSTER_T2.getRegistryName()
        );
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(BLOCK_BOOSTER_T1).setRegistryName(BLOCK_BOOSTER_T1.getRegistryName()));
        event.getRegistry().register(new ItemBlock(BLOCK_BOOSTER_T2).setRegistryName(BLOCK_BOOSTER_T2.getRegistryName()));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        registerItemModel(Item.getItemFromBlock(BLOCK_BOOSTER_T1), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(BLOCK_BOOSTER_T2), 0, "inventory");
    }

    @SideOnly(Side.CLIENT)
    public void registerItemModel(@Nonnull Item item, int meta, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), variant));
    }
}