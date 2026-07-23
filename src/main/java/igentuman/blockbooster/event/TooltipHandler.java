package igentuman.blockbooster.event;

import igentuman.blockbooster.block.IBoosterBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import static igentuman.blockbooster.BlockBooster.MODID;

/**
 * Adds booster description tooltips to booster block items.
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class TooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof IBoosterBlock booster) {
            booster.appendBoosterTooltip(event.getToolTip());
        }
    }
}
