package igentuman.blockbooster.setup;

import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.client.BoosterRenderer;
import igentuman.blockbooster.client.BoosterScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BlockBooster.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static void init(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(Registration.BLOCKBOOSTER_CONTAINER.get(), BoosterScreen::new);
            ItemBlockRenderTypes.setRenderLayer(Registration.BLOCKBOOSTER.get(), RenderType.translucent());
            BoosterRenderer.register();
        });
       
    }

}
