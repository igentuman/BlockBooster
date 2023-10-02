package igentuman.blockbooster.setup;

import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.client.screen.BoosterManaScreen;
import igentuman.blockbooster.client.screen.BoosterT1Screen;
import igentuman.blockbooster.client.screen.BoosterT2Screen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static igentuman.blockbooster.BlockBooster.MODID;
import static igentuman.blockbooster.setup.Registration.BLOCKBOOSTER_T1;
import static igentuman.blockbooster.setup.Registration.BLOCKBOOSTER_T1_ITEM;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final List<Supplier<? extends ItemLike>> BOOSTER_TAB_ITEMS = new ArrayList<>();
    public static final RegistryObject<CreativeModeTab> BOOSTER_TAB = TABS.register("blockbooster",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.blockbooster"))
                    .icon(BLOCKBOOSTER_T1_ITEM.get()::getDefaultInstance)
                    .displayItems((displayParams, output) ->
                            BOOSTER_TAB_ITEMS.forEach(itemLike -> output.accept(itemLike.get())))
                    .withSearchBar()
                    .build()
    );

    public static void init(FMLClientSetupEvent event) {

        event.enqueueWork(() -> {
            MenuScreens.register(Registration.BLOCKBOOSTER_T1_CONTAINER.get(), BoosterT1Screen::new);
            MenuScreens.register(Registration.BLOCKBOOSTER_T2_CONTAINER.get(), BoosterT2Screen::new);
            MenuScreens.register(Registration.BLOCKBOOSTER_MANA_CONTAINER.get(), BoosterManaScreen::new);
        });
       
    }

}
