package igentuman.blockbooster.network;


import igentuman.blockbooster.container.ContainerBlockBooster;
import igentuman.blockbooster.gui.GuiBlockBooster;
import igentuman.blockbooster.tile.TileBlockBooster;
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
        if (te instanceof TileBlockBooster) {
            return new ContainerBlockBooster((TileBlockBooster) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileBlockBooster) {
            TileBlockBooster containerTileEntity = (TileBlockBooster) te;
            return new GuiBlockBooster(new ContainerBlockBooster(containerTileEntity));
        }
        return null;
    }
}