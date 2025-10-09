package igentuman.blockbooster.util;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Tracks server TPS (Ticks Per Second) to detect lag
 */
@Mod.EventBusSubscriber
public class TPSTracker {
    private static final int SAMPLE_SIZE = 20; // Sample over 1 second (20 ticks)
    private static long[] tickTimes = new long[SAMPLE_SIZE];
    private static int currentIndex = 0;
    private static long lastTickTime = 0;
    private static double currentTPS = 20.0;
    
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            long currentTime = System.currentTimeMillis();
            
            if (lastTickTime > 0) {
                long tickTime = currentTime - lastTickTime;
                tickTimes[currentIndex] = tickTime;
                currentIndex = (currentIndex + 1) % SAMPLE_SIZE;
                
                // Calculate average tick time
                long totalTime = 0;
                for (long time : tickTimes) {
                    totalTime += time;
                }
                double averageTickTime = (double) totalTime / SAMPLE_SIZE;
                
                // Calculate TPS (1000ms / average tick time in ms)
                if (averageTickTime > 0) {
                    currentTPS = Math.min(20.0, 1000.0 / averageTickTime);
                }
            }
            
            lastTickTime = currentTime;
        }
    }
    
    /**
     * Get the current server TPS
     * @return Current TPS (0-20)
     */
    public static double getCurrentTPS() {
        return currentTPS;
    }
    
    /**
     * Check if TPS is above the threshold
     * @param threshold Minimum acceptable TPS
     * @return true if TPS is acceptable
     */
    public static boolean isTpsAcceptable(double threshold) {
        return currentTPS >= threshold;
    }
    
    /**
     * Reset the tracker (useful for testing)
     */
    public static void reset() {
        tickTimes = new long[SAMPLE_SIZE];
        currentIndex = 0;
        lastTickTime = 0;
        currentTPS = 20.0;
    }
}