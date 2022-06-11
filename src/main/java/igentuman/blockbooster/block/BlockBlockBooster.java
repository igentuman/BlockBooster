package igentuman.blockbooster.block;

import igentuman.blockbooster.BlockBooster;
import igentuman.blockbooster.ModInfo;
import igentuman.blockbooster.tile.TileBlockBooster;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


public class BlockBlockBooster extends BlockHorizontal {
    public BlockBlockBooster() {
        super(Material.IRON);
        this.setHardness(3.5f);
        this.setResistance(17.5f);
        this.setTranslationKey("booster");
        this.setRegistryName(ModInfo.MODID, "booster");
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }


    @NotNull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
    }

    @NotNull
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing face, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState state) {
        return new TileBlockBooster();
    }


    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = worldIn.getTileEntity(pos);
        if(!(te instanceof TileBlockBooster)) {
            return true;
        }

        if(worldIn.isRemote) {
            return true;
        }

        playerIn.openGui(BlockBooster.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
}
