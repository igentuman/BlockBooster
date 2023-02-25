package igentuman.blockbooster.network;

import igentuman.blockbooster.BlockBooster;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class TileBoosterUpdatePacket implements IMessage {

    public int energyStored;
    public boolean isWorking;
    public boolean isRedstonePowered;
    public BlockPos pos;
    public byte[] boostFLag;
    public int length;

    public TileBoosterUpdatePacket() {
    }

    public TileBoosterUpdatePacket(BlockPos pos, int energyStored, boolean isWorking, boolean isRedstonePowered, byte[] boostFLag, int length) {
        this.pos = pos;
        this.energyStored = energyStored;
        this.isWorking = isWorking;
        this.isRedstonePowered = isRedstonePowered;
        this.boostFLag = boostFLag;
        this.length = length;
    }

    public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.energyStored = buf.readInt();
        this.isWorking = buf.readBoolean();
        this.isRedstonePowered = buf.readBoolean();
        this.length = buf.readByte();
        this.boostFLag = readBytes(buf);
    }

    private byte[] readBytes(ByteBuf buf)
    {
        byte[] tmp = new byte[length];
        for(int i=0;i<length;i++) {
            tmp[i] = buf.readByte();
        }
        return tmp;
    }

    private void writeBytes(ByteBuf buf)
    {
        for(int i=0;i<length;i++) {
            buf.writeByte(boostFLag[i]);
        }
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeInt(this.energyStored);
        buf.writeBoolean(this.isWorking);
        buf.writeBoolean(this.isRedstonePowered);
        buf.writeByte(this.length);
        writeBytes(buf);
    }

    public static class Handler implements IMessageHandler<TileBoosterUpdatePacket, IMessage> {

        @Override
        public IMessage onMessage(TileBoosterUpdatePacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                BlockBooster.proxy.handleProcessUpdatePacket(message, ctx);
            });
            return null;
        }
    }
}
