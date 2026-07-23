package igentuman.blockbooster.util;

import igentuman.blockbooster.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.server.timings.ObjectTimings;
import net.neoforged.neoforge.server.timings.TimeTracker;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockEntityLookTracker {

    private static final Map<UUID, BlockPos> playerLastLookedAt = new HashMap<>();
    private static final double MAX_REACH_DISTANCE = 20.0;

    public static void updatePlayerLook(ServerPlayer player) {
        TimeTracker.BLOCK_ENTITY_UPDATE.enable(30);

        if (player == null || player.level() == null) return;

        UUID playerId = player.getUUID();
        BlockPos lookedAtPos = getBlockEntityPlayerIsLookingAt(player);
        BlockPos lastLookedAt = playerLastLookedAt.get(playerId);

        if (lookedAtPos != null && !lookedAtPos.equals(lastLookedAt)) {
            playerLastLookedAt.put(playerId, lookedAtPos);
            BlockEntity blockEntity = player.level().getBlockEntity(lookedAtPos);
            if (blockEntity != null) {
                checkAndDisplayBlockEntityInfo(player, blockEntity);
            }
        } else if (lookedAtPos == null && lastLookedAt != null) {
            playerLastLookedAt.remove(playerId);
            clearPlayerTitle(player);
        }
    }

    private static BlockPos getBlockEntityPlayerIsLookingAt(ServerPlayer player) {
        Vec3 eyePosition = player.getEyePosition(1.0F);
        Vec3 lookVector = player.getViewVector(1.0F);
        Vec3 reachVector = eyePosition.add(lookVector.scale(MAX_REACH_DISTANCE));

        BlockHitResult hitResult = player.level().clip(new ClipContext(
                eyePosition, reachVector,
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player
        ));

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = hitResult.getBlockPos();
            if (player.level().getBlockEntity(pos) != null) {
                return pos;
            }
        }
        return null;
    }

    private static void checkAndDisplayBlockEntityInfo(ServerPlayer player, BlockEntity blockEntity) {
        try {
            double thresholdNs = CommonConfig.GENERAL.slow_block_threshold_ns.get();
            for (ObjectTimings<BlockEntity> timing : TimeTracker.BLOCK_ENTITY_UPDATE.getTimingData()) {
                BlockEntity be = timing.getObject().get();
                if (be == blockEntity) {
                    double avgNs = timing.getAverageTimings();
                    if (avgNs > thresholdNs) {
                        displaySlowBlockEntityInfo(player, blockEntity, avgNs / 1_000_000.0);
                    }
                    return;
                }
            }
        } catch (Exception e) {
            // silently ignore
        }
    }

    private static void displaySlowBlockEntityInfo(ServerPlayer player, BlockEntity blockEntity, double avgMs) {
        String blockName = blockEntity.getBlockState().getBlock().asItem().toString();
        if (blockName.contains(":")) {
            blockName = blockName.substring(blockName.lastIndexOf(":") + 1);
        }
        Component message = Component.literal(String.format("§c⚠ %s §7- §c%.2fms", blockName, avgMs));
        player.connection.send(new ClientboundSetActionBarTextPacket(message));
    }

    private static void clearPlayerTitle(ServerPlayer player) {
        player.connection.send(new ClientboundClearTitlesPacket(false));
    }

    public static void removePlayer(UUID playerId) {
        playerLastLookedAt.remove(playerId);
    }

    public static void clear() {
        playerLastLookedAt.clear();
    }
}
