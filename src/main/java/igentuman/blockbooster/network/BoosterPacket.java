package igentuman.blockbooster.network;

import igentuman.blockbooster.tile.ITileBooster;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class BoosterPacket implements CustomPacketPayload {

    public static final Identifier ID = Identifier.parse("blockbooster:booster_packet");
    public static final Type<BoosterPacket> TYPE = new Type<>(ID);

    private BlockPos pos;
    private long posKey;
    private boolean val;

    public BoosterPacket(BlockPos pos, long posKey, boolean val)
    {
        this.pos = pos;
        this.posKey = posKey;
        this.val = val;
    }

    public BoosterPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.posKey = buf.readLong();
        this.val = buf.readBoolean();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeLong(posKey);
        buf.writeBoolean(val);
    }

    public static void handle(BoosterPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            // NeoForge 1.21: context.player() returns a generic Player, cast to ServerPlayer for server handling
            if (context.player() instanceof ServerPlayer player) {
                BlockEntity be = player.level().getBlockEntity(packet.pos);
                if(be instanceof ITileBooster) {
                    // For T3 boosters, use the posKey directly
                    // For T1/T2 boosters, the posKey will be a small value (direction ordinal)
                    ((ITileBooster) be).setIndexStatus(packet.posKey, packet.val);
                }
            }
        });
    }

    // NeoForge 1.21: CustomPacketPayload interface requires id() and type() methods
    public @NotNull Identifier id() {
        return ID;
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
