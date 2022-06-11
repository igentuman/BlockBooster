package igentuman.blockbooster.proxy;

import igentuman.blockbooster.network.TileProcessUpdatePacket;
import igentuman.blockbooster.tile.TileBlockBooster;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy implements ISidedProxy {

    @Override
    public void handleProcessUpdatePacket(TileProcessUpdatePacket message, MessageContext ctx) {
        TileEntity te = Minecraft.getMinecraft().world.getTileEntity(message.pos);
        if(te instanceof TileBlockBooster) {
            ((TileBlockBooster) te).onTileUpdatePacket(message);
        }
    }
}
