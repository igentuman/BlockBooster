package igentuman.blockbooster.event;

import igentuman.blockbooster.util.BlockEntityLookTracker;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import static igentuman.blockbooster.BlockBooster.MODID;

/**
 * Handles player tick events to track chunk changes and block entity looks
 */
@EventBusSubscriber(modid = MODID)
public class PlayerTickHandler {
    
    private static int lookCheckCounter = 0;
    private static final int LOOK_CHECK_INTERVAL = 10; // Check every 10 ticks (0.5 seconds)
    
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        // Only process on server side
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {

            // Check what block entity player is looking at more frequently
            lookCheckCounter++;
            if (lookCheckCounter >= LOOK_CHECK_INTERVAL) {
                lookCheckCounter = 0;
                BlockEntityLookTracker.updatePlayerLook(serverPlayer);
            }
        }
    }
    
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        BlockEntityLookTracker.removePlayer(event.getEntity().getUUID());
    }
}