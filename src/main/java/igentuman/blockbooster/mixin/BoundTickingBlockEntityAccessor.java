package igentuman.blockbooster.mixin;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor for BoundTickingBlockEntity to get the block entity
 */
@Mixin(targets = "net.minecraft.world.level.chunk.LevelChunk$BoundTickingBlockEntity")
public interface BoundTickingBlockEntityAccessor {
    
    @Accessor("blockEntity")
    BlockEntity getBlockEntity();
}