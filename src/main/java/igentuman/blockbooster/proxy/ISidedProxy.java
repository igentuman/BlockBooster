package igentuman.blockbooster.proxy;

import igentuman.blockbooster.network.TileProcessUpdatePacket;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface ISidedProxy {

    default void preInit(FMLPreInitializationEvent event) {
    }

    default void init(FMLInitializationEvent event) {
       // EvTweaksRecipes.init();
    }

    void handleProcessUpdatePacket(TileProcessUpdatePacket message, MessageContext context);

}