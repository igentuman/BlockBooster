package igentuman.blockbooster.command;

import igentuman.blockbooster.ModConfig;
import igentuman.blockbooster.util.BlockUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;

import static igentuman.blockbooster.ModInfo.MODID;


public class CommandAddToBlackList extends CommandBase {
	
	@Override
	public String getName() {
		return "booster_add_to_blacklist";
	}
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "/booster_add_to_blacklist";
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
				String teName = BlockUtil.getBlockIdLine(te);
				String[] tmp = new String[ModConfig.general.black_list.length+1];
				tmp[tmp.length-1] = teName;
				ModConfig.general.black_list = tmp;
				ConfigManager.sync(MODID, Config.Type.INSTANCE);

				notifyCommandListener(sender, this, "Blacklisted: " + teName);
			}
		}
	}
}
