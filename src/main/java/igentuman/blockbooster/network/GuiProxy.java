package igentuman.blockbooster.network;


import igentuman.blockbooster.container.ContainerBlockBoosterT1;
import igentuman.blockbooster.gui.GuiBlockBoosterT1;
import igentuman.blockbooster.tile.TileBlockBoosterT1;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiProxy implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileBlockBoosterT1) {
            return new ContainerBlockBoosterT1((TileBlockBoosterT1) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileBlockBoosterT1) {
            return new GuiBlockBoosterT1(new ContainerBlockBoosterT1((TileBlockBoosterT1) te));
        }
        return null;
    }
}