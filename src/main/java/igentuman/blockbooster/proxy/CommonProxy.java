package igentuman.blockbooster.proxy;

import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.network.TileProcessUpdatePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy implements ISidedProxy {

  public void preInit(FMLPreInitializationEvent event) {
  }

  public void init(FMLInitializationEvent event) {

  }

  @Override
  public void handleProcessUpdatePacket(TileProcessUpdatePacket message, MessageContext ctx) {
    BlockBooster.instance.logger.error("Got PacketUpdateItemStack on wrong side!");
  }
}