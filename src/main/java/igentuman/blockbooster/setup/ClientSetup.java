package igentuman.blockbooster.setup;

import igentuman.blockbooster.client.screen.BoosterT1Screen;
import igentuman.blockbooster.client.screen.BoosterT2Screen;
import igentuman.blockbooster.client.screen.BoosterT3Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import static igentuman.blockbooster.BlockBooster.MODID;
import static igentuman.blockbooster.setup.Registration.*;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ClientSetup {

    public static void init(FMLClientSetupEvent event) {
        // Client setup complete
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(BLOCKBOOSTER_T1_CONTAINER.get(), BoosterT1Screen::new);
        event.register(BLOCKBOOSTER_T2_CONTAINER.get(), BoosterT2Screen::new);
        event.register(BLOCKBOOSTER_T3_CONTAINER.get(), BoosterT3Screen::new);
    }

}
