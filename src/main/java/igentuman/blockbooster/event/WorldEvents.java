package igentuman.blockbooster.event;

import igentuman.blockbooster.block.IBoosterBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;

import static igentuman.blockbooster.BlockBooster.MODID;
import static igentuman.blockbooster.config.CommonConfig.GENERAL;

@EventBusSubscriber(modid = MODID)
public class WorldEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        boolean placed = true;
        BlockState state = event.getState();
        if(state == null) return;
        if(state.getBlock() instanceof IBoosterBlock booster) {
            placed = IBoosterBlock.processBlockPlace(event.getLevel(), event.getPos(), event.getPlacedBlock(), state, event.getPlacedAgainst());
        }
        if(!placed) {
            event.setCanceled(true);
            if (event.getEntity() instanceof net.minecraft.world.entity.player.Player player) {
                player.sendSystemMessage(Component.translatable("booster.limit_message", GENERAL.boosters_per_chunk.get()));
            }
        }
    }

}
