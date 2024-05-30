package igentuman.blockbooster.block;

import igentuman.blockbooster.tile.ITileBooster;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static igentuman.blockbooster.config.CommonConfig.GENERAL;

public interface IBoosterBlock {

    static boolean processBlockPlace(LevelAccessor level, BlockPos pos, BlockState block, BlockState blockState, BlockState attachment)
    {
        int boosters = 0;
        for(BlockPos entPos: level.getChunk(pos).getBlockEntitiesPos()) {
            BlockEntity be = level.getBlockEntity(entPos);
            if(be instanceof ITileBooster) {
                boosters++;
                if(boosters > GENERAL.boosters_per_chunk.get()) {
                    return false;
                }
            }
        }
        return true;
    }
}
