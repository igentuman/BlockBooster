package igentuman.blockbooster.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BlockUtil {
    public static String getBlockDataInfo(World world, BlockPos pos)
    {
        IBlockState actualState = world.getBlockState(pos).getActualState(world, pos);
        Block block = actualState.getBlock();

        int id = Block.getIdFromBlock(block);
        int meta = block.getMetaFromState(actualState);
        ResourceLocation rl = ForgeRegistries.BLOCKS.getKey(block);
        String registryName = rl != null ? rl.toString() : "<null>";
        return registryName+":"+id+":"+meta;
    }

    public static String getBlockDataInfo(TileEntity te)
    {
        World world = te.getWorld();
        BlockPos pos = te.getPos();
        return getBlockDataInfo(world, pos);
    }
}
