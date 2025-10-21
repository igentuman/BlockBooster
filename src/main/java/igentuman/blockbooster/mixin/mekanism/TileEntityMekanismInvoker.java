/*
 * DISABLED - Mekanism integration not available for NeoForge 1.21
 * 
 * The onUpdateServer method in Mekanism's TileEntityMekanism cannot be found.
 * This is likely because Mekanism has not yet released a NeoForge 1.21 version.
 * 
 * This invoker can be re-enabled when Mekanism releases NeoForge 1.21 support.
 */
package igentuman.blockbooster.mixin.mekanism;

/*
import mekanism.common.tile.base.TileEntityMekanism;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = TileEntityMekanism.class)
public interface TileEntityMekanismInvoker {

    @Invoker("onUpdateServer")
    void onServerTick();
}
*/

// Placeholder interface to prevent missing reference errors
public interface TileEntityMekanismInvoker {
    void onServerTick();
}
