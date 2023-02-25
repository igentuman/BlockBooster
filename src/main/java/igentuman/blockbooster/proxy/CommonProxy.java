package igentuman.blockbooster.proxy;

import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.network.TileBoosterUpdatePacket;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy implements ISidedProxy {

  public void preInit(FMLPreInitializationEvent event) {
  }

  public void init(FMLInitializationEvent event) {

  }

  @Override
  public void handleProcessUpdatePacket(TileBoosterUpdatePacket message, MessageContext ctx) {
    BlockBooster.instance.logger.error("Got PacketUpdateItemStack on wrong side!");
  }
}