package igentuman.blockbooster.setup;

import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.network.BoosterPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public class Messages {

    // StreamCodec for serializing and deserializing BoosterPacket
    public static final StreamCodec<RegistryFriendlyByteBuf, BoosterPacket> BOOSTER_STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> packet.write(buf),
                    BoosterPacket::new
            );

    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(BlockBooster.MODID + ":1");

        registrar.playBidirectional(
                BoosterPacket.TYPE,
                BOOSTER_STREAM_CODEC,
                Messages::handleServerPacket,
                Messages::handleClientPacket
        );
    }

    // Handle packet on the client side (if needed for client-specific logic)
    private static void handleClientPacket(BoosterPacket packet, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        // Client-side handling is typically empty for server->client packets
        // But left here for potential future use
    }

    // Handle packet on the server side
    private static void handleServerPacket(BoosterPacket packet, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        BoosterPacket.handle(packet, context);
    }

    // Send packet to the server (client-side only)
    public static void sendToServer(BoosterPacket message) {
        ClientPacketDistributor.sendToServer(message);
    }

    // Send packet to a specific player
    public static void sendToPlayer(BoosterPacket message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }
}
