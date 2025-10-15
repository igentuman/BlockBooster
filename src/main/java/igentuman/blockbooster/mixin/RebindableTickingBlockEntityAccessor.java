package igentuman.blockbooster.mixin;

import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor for RebindableTickingBlockEntityWrapper to get the ticker
 */
@Mixin(targets = "net.minecraft.world.level.chunk.LevelChunk$RebindableTickingBlockEntityWrapper")
public interface RebindableTickingBlockEntityAccessor {
    
    @Accessor("ticker")
    TickingBlockEntity getTicker();
}