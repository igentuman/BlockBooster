package igentuman.blockbooster.network;

import igentuman.blockbooster.tile.ITileBooster;
import igentuman.blockbooster.tile.TileBoosterT1;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BoosterPacket {

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

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeLong(posKey);
        buf.writeBoolean(val);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {

            ServerPlayer player = context.getSender();
            BlockEntity be = player.level().getBlockEntity(pos);
            if(be instanceof ITileBooster) {
                // For T3 boosters, use the posKey directly
                // For T1/T2 boosters, the posKey will be a small value (direction ordinal)
                ((ITileBooster) be).setIndexStatus(posKey, val);
            }
        });
        return true;
    }
}
