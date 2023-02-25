package igentuman.blockbooster.network;

import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.tile.ITileBooster;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.FMLServerHandler;


public class SimpleCommandToServerPacket implements IMessage {

    public BlockPos pos;
    public int id;
    public byte val;

    public SimpleCommandToServerPacket() {
    }

    public SimpleCommandToServerPacket(BlockPos pos, int id, byte val) {
        this.pos = pos;
        this.id = id;
        this.val = val;
    }

    public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.id = buf.readInt();
        this.val = buf.readByte();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(id);
        buf.writeByte(val);
    }

    public static class Handler implements IMessageHandler<SimpleCommandToServerPacket, IMessage> {

        @Override
        public IMessage onMessage(SimpleCommandToServerPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {

                TileEntity te = ctx.getServerHandler().player.getServerWorld().getTileEntity(message.pos);
                if(te instanceof ITileBooster) {
                    ((ITileBooster) te).setBoostFlagValue(message.id, message.val);
                }
            });
            return null;
        }
    }
}
