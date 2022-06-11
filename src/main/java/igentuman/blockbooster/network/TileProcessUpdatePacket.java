package igentuman.blockbooster.network;

import igentuman.blockbooster.BlockBooster;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class TileProcessUpdatePacket implements IMessage {

    public int energyStored;
    public boolean isWorking;
    public boolean isRedstonePowered;
    public BlockPos pos;

    public TileProcessUpdatePacket() {
    }

    public TileProcessUpdatePacket(BlockPos pos, int energyStored, boolean isWorking, boolean isRedstonePowered) {
        this.pos = pos;
        this.energyStored = energyStored;
        this.isWorking = isWorking;
        this.isRedstonePowered = isRedstonePowered;
    }

    public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.energyStored = buf.readInt();
        this.isWorking = buf.readBoolean();
        this.isRedstonePowered = buf.readBoolean();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeInt(this.energyStored);
        buf.writeBoolean(this.isWorking);
        buf.writeBoolean(this.isRedstonePowered);

    }

    public static class Handler implements IMessageHandler<TileProcessUpdatePacket, IMessage> {

        @Override
        public IMessage onMessage(TileProcessUpdatePacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                BlockBooster.proxy.handleProcessUpdatePacket(message, ctx);
            });
            return null;
        }
    }
}
