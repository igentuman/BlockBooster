package igentuman.blockbooster.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BoosterUtil {

    /**
     * Boosts a block entity by calling its ticker multiple times
     * @param level The world level
     * @param pos The position of the block entity
     * @param be The block entity to boost
     * @param boostRate The number of times to call the ticker
     * @return true if the block entity was successfully boosted, false otherwise
     */
    public static boolean BoostBlockEntity(Level level, BlockPos pos, BlockEntity be, int boostRate) {
        if (level == null || be == null || be.isRemoved()) {
            return false;
        }

        BlockEntityTicker<BlockEntity> ticker = be.getBlockState()
                .getTicker(level, (BlockEntityType<BlockEntity>) be.getType());
        
        if (ticker != null) {
            for (int i = 0; i < boostRate; i++) {
                ticker.tick(level, be.getBlockPos(), be.getBlockState(), be);
            }
            return true;
        }
        
        return false;
    }

    /**
     * Boosts a block entity and measures the time taken
     * @param level The world level
     * @param pos The position of the block entity
     * @param be The block entity to boost
     * @param boostRate The number of times to call the ticker
     * @return BoostResult containing success status and time taken in nanoseconds
     */
    public static BoostResult BoostBlockEntityWithTiming(Level level, BlockPos pos, BlockEntity be, int boostRate) {
        if (level == null || be == null || be.isRemoved()) {
            return new BoostResult(false, 0);
        }

        BlockEntityTicker<BlockEntity> ticker = be.getBlockState()
                .getTicker(level, (BlockEntityType<BlockEntity>) be.getType());
        
        if (ticker != null) {
            long startTime = System.nanoTime();
            for (int i = 0; i < boostRate; i++) {
                ticker.tick(level, be.getBlockPos(), be.getBlockState(), be);
            }
            long endTime = System.nanoTime();
            return new BoostResult(true, endTime - startTime);
        }
        
        return new BoostResult(false, 0);
    }

    public static class BoostResult {
        public final boolean success;
        public final long timeNanos;

        public BoostResult(boolean success, long timeNanos) {
            this.success = success;
            this.timeNanos = timeNanos;
        }
    }
}
