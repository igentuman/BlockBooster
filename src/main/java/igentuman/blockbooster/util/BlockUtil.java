package igentuman.blockbooster.util;

import igentuman.blockbooster.BlockBooster;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Random;

public class BlockUtil {

    public static ItemStack getStackForBlock(TileEntity te)
    {
        if(te == null) return ItemStack.EMPTY;
        if(BlockBooster.hooks.IC2Loaded) {
            if (te instanceof ic2.core.block.TileEntityBlock) {
                ic2.core.block.ITeBlock teb = ic2.core.block.TeBlockRegistry.get(((ic2.core.block.TileEntityBlock) te.getWorld().getTileEntity(te.getPos())).getClass());
                return ((ic2.core.block.BlockTileEntity) te.getBlockType()).getItemStack(teb);
            }
        }
        IBlockState actualState = te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos());
        ItemStack st = new ItemStack(te.getBlockType().getItemDropped(actualState, new Random(), 1));
        if(BlockBooster.hooks.MekanismLoaded) {
            if(te.getClass().getName().contains("mekanism")) {
                st.setItemDamage(te.getBlockType().getMetaFromState(actualState));
            }
        }

        st.setTagCompound(te.getTileData());
        st.setTagCompound(te.getUpdateTag());
        return st;
    }

    public static String getBlockIdLine(World world, BlockPos pos)
    {
        IBlockState actualState = world.getBlockState(pos).getActualState(world, pos);
        Block block = actualState.getBlock();

        int id = Block.getIdFromBlock(block);
        int meta = block.getMetaFromState(actualState);

        ResourceLocation rl = ForgeRegistries.BLOCKS.getKey(block);
        String registryName = rl != null ? rl.toString() : "<null>";

        if (registryName.equals("ic2:te") && BlockBooster.hooks.IC2Loaded) {
            TileEntity te = world.getTileEntity(pos);
            if(te instanceof ic2.core.block.TileEntityBlock) {
                ic2.core.block.ITeBlock teb = ic2.core.block.TeBlockRegistry.get(((ic2.core.block.TileEntityBlock)world.getTileEntity(pos)).getClass());
                meta = teb.getId();
            }
        }
        return registryName+":"+id+":"+meta;
    }

    public static String getBlockIdLine(TileEntity te)
    {
        World world = te.getWorld();
        BlockPos pos = te.getPos();
        return getBlockIdLine(world, pos);
    }
}
