package igentuman.blockbooster.setup;

import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.client.screen.BoosterT1Screen;
import igentuman.blockbooster.client.screen.BoosterT2Screen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BlockBooster.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static void init(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(Registration.BLOCKBOOSTER_T1_CONTAINER.get(), BoosterT1Screen::new);
            MenuScreens.register(Registration.BLOCKBOOSTER_T2_CONTAINER.get(), BoosterT2Screen::new);
        });
       
    }

}
