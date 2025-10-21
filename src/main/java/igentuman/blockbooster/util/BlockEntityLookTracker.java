package igentuman.blockbooster.util;

import igentuman.blockbooster.config.CommonConfig;
import igentuman.blockbooster.mixin.BoundTickingBlockEntityAccessor;
import igentuman.blockbooster.mixin.RebindableTickingBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks what block entity a player is looking at and displays performance info
 */
public class BlockEntityLookTracker {
    
    private static final Map<UUID, BlockPos> playerLastLookedAt = new HashMap<>();
    
    private static final double SLOW_BLOCK_ENTITY_THRESHOLD = CommonConfig.GENERAL.slow_block_threshold_ns.get() / 1_000_000.0;
    
    // Maximum distance to raycast (in blocks)
    private static final double MAX_REACH_DISTANCE = 20.0;
    
    /**
     * Update what block entity the player is looking at and display info if it's slow
     */
    public static void updatePlayerLook(ServerPlayer player) {
        if (player == null || player.level() == null) {
            return;
        }
        
        UUID playerId = player.getUUID();
        BlockPos lookedAtPos = getBlockEntityPlayerIsLookingAt(player);
        BlockPos lastLookedAt = playerLastLookedAt.get(playerId);
        
        // Check if player is looking at a different block entity
        if (lookedAtPos != null && !lookedAtPos.equals(lastLookedAt)) {
            playerLastLookedAt.put(playerId, lookedAtPos);
            
            // Get the block entity and check if it's slow
            BlockEntity blockEntity = player.level().getBlockEntity(lookedAtPos);
            if (blockEntity != null) {
                checkAndDisplayBlockEntityInfo(player, blockEntity, lookedAtPos);
            }
        } else if (lookedAtPos == null && lastLookedAt != null) {
            // Player stopped looking at a block entity
            playerLastLookedAt.remove(playerId);
            clearPlayerTitle(player);
        }
    }
    
    /**
     * Get the position of the block entity the player is looking at
     */
    private static BlockPos getBlockEntityPlayerIsLookingAt(ServerPlayer player) {
        Vec3 eyePosition = player.getEyePosition(1.0F);
        Vec3 lookVector = player.getViewVector(1.0F);
        Vec3 reachVector = eyePosition.add(lookVector.scale(MAX_REACH_DISTANCE));
        
        // Perform ray trace
        BlockHitResult hitResult = player.level().clip(new ClipContext(
            eyePosition,
            reachVector,
            ClipContext.Block.OUTLINE,
            ClipContext.Fluid.NONE,
            player
        ));
        
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = hitResult.getBlockPos();
            
            // Check if there's a block entity at this position
            if (player.level().getBlockEntity(pos) != null) {
                return pos;
            }
        }
        
        return null;
    }
    
    /**
     * Check if the block entity is slow and display info to the player
     */
    private static void checkAndDisplayBlockEntityInfo(ServerPlayer player, BlockEntity blockEntity, BlockPos pos) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        
        try {
            // Try to find the ticking block entity data
            LevelChunk chunk = serverLevel.getChunkAt(pos);
            BlockEntityTickDataAccessor tickData = findTickingBlockEntityData(chunk, blockEntity);
            
            if (tickData != null) {
                double avgTickTime = tickData.world_balance$getAvgTickTime();
                long tickCount = tickData.world_balance$getTickCount();

                // Only display if the block entity has been ticked at least once
                if (tickCount > 0) {
                    boolean isSlow = avgTickTime > SLOW_BLOCK_ENTITY_THRESHOLD;
                    displayBlockEntityInfo(player, blockEntity, avgTickTime, tickCount, isSlow);
                }
            }
        } catch (Exception e) {
            // Silently ignore errors
        }
    }
    
    /**
     * Find the TickingBlockEntity data for a specific BlockEntity
     * NOTE: This method is disabled in NeoForge 1.21 as LevelChunk.tickersInLevel is now private
     * and RebindableTickingBlockEntityWrapper is not accessible from outside the package
     */
    private static BlockEntityTickDataAccessor findTickingBlockEntityData(LevelChunk chunk, BlockEntity targetBlockEntity) {
        try {
            // Access to LevelChunk internals has been restricted in NeoForge 1.21
            // This functionality would need to be reimplemented using public API or Mixins
            /*
            var tickers =  chunk.tickersInLevel;
            
            if (tickers != null) {
                for (TickingBlockEntity ticker : tickers.values()) {
                    if (ticker instanceof LevelChunk.RebindableTickingBlockEntityWrapper wrapper) {
                        // Get the actual ticker from the wrapper
                        TickingBlockEntity actualTicker = ((RebindableTickingBlockEntityAccessor) wrapper).getTicker();
                        
                        // Check if the actual ticker is a BoundTickingBlockEntity and get its BlockEntity
                        if (actualTicker instanceof BoundTickingBlockEntityAccessor boundAccessor) {
                            BlockEntity be = boundAccessor.getBlockEntity();
                            if (be == targetBlockEntity && actualTicker instanceof BlockEntityTickDataAccessor tickData) {
                                return tickData;
                            }
                        }
                    } else if (ticker instanceof BoundTickingBlockEntityAccessor boundAccessor) {
                        // Direct BoundTickingBlockEntity (not wrapped)
                        BlockEntity be = boundAccessor.getBlockEntity();
                        if (be == targetBlockEntity && ticker instanceof BlockEntityTickDataAccessor tickData) {
                            return tickData;
                        }
                    }
                }
            }
            */
        } catch (Exception e) {
            // Silently ignore reflection errors
        }
        
        return null;
    }
    
    /**
     * Display block entity performance info to the player
     */
    private static void displayBlockEntityInfo(ServerPlayer player, BlockEntity blockEntity, 
                                               double avgTickTime, long tickCount, boolean isSlow) {
        String blockName = blockEntity.getBlockState().getBlock().asItem().toString();
        // Extract just the block type name (remove namespace)
        if (blockName.contains(":")) {
            blockName = blockName.substring(blockName.lastIndexOf(":") + 1);
        }
        
        if (isSlow) {
            Component message = Component.literal(String.format(
                "§c⚠ %s §7- §c%.2fms",
                blockName,
                avgTickTime
            ));
            
            // Send action bar message to player
            player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket(message));
        }
    }
    
    /**
     * Clear the title display for a player
     */
    private static void clearPlayerTitle(ServerPlayer player) {
        player.connection.send(new net.minecraft.network.protocol.game.ClientboundClearTitlesPacket(false));
    }
    
    /**
     * Remove player from tracking when they disconnect
     */
    public static void removePlayer(UUID playerId) {
        playerLastLookedAt.remove(playerId);
    }
    
    /**
     * Clear all tracking data
     */
    public static void clear() {
        playerLastLookedAt.clear();
    }
}