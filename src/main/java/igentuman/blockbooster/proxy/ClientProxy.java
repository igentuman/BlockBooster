package igentuman.blockbooster.proxy;

import igentuman.blockbooster.network.TileBoosterUpdatePacket;
import igentuman.blockbooster.tile.TileBlockBoosterT1;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy implements ISidedProxy {

    @Override
    public void handleProcessUpdatePacket(TileBoosterUpdatePacket message, MessageContext ctx) {
        TileEntity te = Minecraft.getMinecraft().world.getTileEntity(message.pos);
        if(te instanceof TileBlockBoosterT1) {
            ((TileBlockBoosterT1) te).onTileUpdatePacket(message);
        }
    }
}
