package igentuman.blockbooster.util;

// Optional mixin integrations disabled - waiting for NeoForge 1.21 compatibility
// import igentuman.blockbooster.mixin.mm.MachineControllerBlockEntityInvoker;
// import igentuman.blockbooster.mixin.mekanism.TileEntityMekanismInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashSet;

import static igentuman.blockbooster.util.ModUtil.*;

public class BoosterUtil {

    public static HashSet<Block> getBlocksByTagKey(String key)
    {
        HashSet<Block> tmp = new HashSet<>();
        TagKey<Block> tag = TagKey.create(BuiltInRegistries.BLOCK.key(), net.minecraft.resources.Identifier.tryParse(key));
        for (var holder : BuiltInRegistries.BLOCK.getTagOrEmpty(tag)) {
            tmp.add(holder.value());
        }
        return tmp;
    }

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
                // Mekanism integration disabled - waiting for NeoForge 1.21 compatibility
                // if(isMekanismLoaded() && be instanceof mekanism.common.tile.base.TileEntityMekanism mekTile) {
                //     ((TileEntityMekanismInvoker)mekTile).onServerTick();
                //     continue;
                // }

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
