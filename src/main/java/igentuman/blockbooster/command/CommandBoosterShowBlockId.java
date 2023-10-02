package igentuman.blockbooster.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;

public class CommandBoosterShowBlockId {

    private CommandBoosterShowBlockId() {}

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        MinecraftForge.EVENT_BUS.register(CommandBoosterShowBlockId.class);
        return Commands.literal("booster_show_block_id")
                .executes(ctx -> {
            return execute(ctx.getSource());
        });
    }

    public static int execute(CommandSourceStack ctx) {
        ServerPlayer pl = ctx.getPlayer();
        HitResult hitResult = rayTrace(pl, 5);
        if(hitResult.getType().equals(HitResult.Type.BLOCK)) {
            BlockEntity be = pl.level().getBlockEntity(((BlockHitResult) hitResult).getBlockPos());
            if(be == null) return 0;
            pl.sendSystemMessage(Component.literal(getBlockName(be)));
        }
            return 0;
    }

    public static String getBlockName(BlockEntity be)
    {
        return ForgeRegistries.BLOCKS.getKey(be.getBlockState().getBlock()).toString();
    }

    public static BlockHitResult rayTrace(Player player, double reach) {
        return rayTrace(player, reach, ClipContext.Fluid.NONE);
    }

    public static BlockHitResult rayTrace(Player player, double reach, ClipContext.Fluid fluidMode) {
        Vec3 headVec = getHeadVec(player);
        Vec3 lookVec = player.getViewVector(1);
        Vec3 endVec = headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        return player.getCommandSenderWorld().clip(new ClipContext(headVec, endVec, ClipContext.Block.OUTLINE, fluidMode, player));
    }

    private static Vec3 getHeadVec(Player player) {
        double posY = player.getY() + player.getEyeHeight();
        if (player.isCrouching()) {
            posY -= 0.08;
        }
        return new Vec3(player.getX(), posY, player.getZ());
    }
}
