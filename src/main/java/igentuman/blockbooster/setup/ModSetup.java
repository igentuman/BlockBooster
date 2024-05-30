package igentuman.blockbooster.setup;

import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.event.WorldEvents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = BlockBooster.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {

    public static final String TAB_NAME = "blockbooster";

    public static void setup() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.register(new WorldEvents());
    }

    public static void init(FMLCommonSetupEvent event) {
        Messages.register();
    }
}
