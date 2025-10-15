package igentuman.blockbooster.mixin.mekanism;

import mekanism.common.tile.base.TileEntityMekanism;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = TileEntityMekanism.class)
public interface TileEntityMekanismInvoker {

    @Invoker("onUpdateServer")
    void onServerTick();
}
