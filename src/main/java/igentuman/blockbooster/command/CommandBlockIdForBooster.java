package igentuman.blockbooster.command;

import igentuman.blockbooster.util.BlockUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;


public class CommandBlockIdForBooster extends CommandBase {
	
	@Override
	public String getName() {
		return "block_id_for_booster";
	}
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "/block_id_for_booster";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
		RayTraceResult res = ((EntityPlayerMP) sender).rayTrace(4, 1);
		if(res.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
			TileEntity te = ((EntityPlayerMP) sender).world.getTileEntity(res.getBlockPos());
			if(te != null) {
				notifyCommandListener(sender, this, BlockUtil.getBlockDataInfo(te));
			}
		}
	}
}
