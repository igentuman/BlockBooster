package igentuman.blockbooster.network;

import igentuman.blockbooster.tile.TileBoosterT1;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BoosterPacket {

    private BlockPos pos;
    private int id;
    private byte val;

    public BoosterPacket(BlockPos pos, int id, byte val)
    {
        this.pos = pos;
        this.id = id;
        this.val = val;
    }

    public BoosterPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.id = buf.readInt();
        this.val = buf.readByte();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(id);
        buf.writeByte(val);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {

            ServerPlayer player = context.getSender();
            BlockEntity be = player.level.getBlockEntity(pos);
            if(be instanceof TileBoosterT1) {
                ((TileBoosterT1) be).setIndexStatus(id, val);
            }
        });
        return true;
    }
}
