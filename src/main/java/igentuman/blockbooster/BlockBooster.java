package igentuman.blockbooster;

import igentuman.blockbooster.command.CommandBoosterShowBlockId;
import igentuman.blockbooster.config.CommonConfig;
import igentuman.blockbooster.event.PlayerTickHandler;
import igentuman.blockbooster.setup.ModSetup;
import igentuman.blockbooster.setup.ClientSetup;
import igentuman.blockbooster.setup.Registration;
import igentuman.blockbooster.setup.Messages;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import static igentuman.blockbooster.setup.Registration.TABS;

@Mod(BlockBooster.MODID)
public class BlockBooster {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "blockbooster";



    public BlockBooster(IEventBus modEventBus, ModContainer modContainer) {
        // Register config
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.spec);
        
        // Setup
        //ModSetup.setup(modEventBus);
        Registration.init(modEventBus);
        
        // Register event handlers
        NeoForge.EVENT_BUS.register(PlayerTickHandler.class);

        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        
        // Add listeners
        modEventBus.addListener(Messages::register);
        
        // Client-only setup
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(ClientSetup::init);
        }
        
        // Config listener
        modEventBus.addListener(this::onModConfigEvent);
    }

    private void onModConfigEvent(final ModConfigEvent event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON)
            CommonConfig.setLoaded();
    }

    private void registerCommands(RegisterCommandsEvent event) {
        //event.getDispatcher().register(CommandBoosterShowBlockId.register());
    }
}
