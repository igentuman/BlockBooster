package igentuman.blockbooster.event;

import igentuman.blockbooster.block.IBoosterBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static igentuman.blockbooster.BlockBooster.MODID;
import static igentuman.blockbooster.config.CommonConfig.GENERAL;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        boolean placed = true;
        BlockState state = event.getState();
        if(state == null) return;
        if(state.getBlock() instanceof IBoosterBlock booster) {
            placed = IBoosterBlock.processBlockPlace(event.getLevel(), event.getPos(), event.getPlacedBlock(), state, event.getPlacedAgainst());
        }
        if(!placed) {
            event.setCanceled(true);
            event.getEntity().sendSystemMessage(Component.translatable("booster.limit_message", GENERAL.boosters_per_chunk.get()));
        }
    }

}
