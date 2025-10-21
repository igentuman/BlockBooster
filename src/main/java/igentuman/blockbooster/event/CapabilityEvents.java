package igentuman.blockbooster.event;

import igentuman.blockbooster.setup.Registration;
import igentuman.blockbooster.tile.*;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import static igentuman.blockbooster.BlockBooster.MODID;

@EventBusSubscriber(modid = MODID)
public class CapabilityEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                Registration.BLOCKBOOSTER_T1_BE.get(),
                (be, side) -> be.energy
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                Registration.BLOCKBOOSTER_T2_BE.get(),
                (be, side) -> be.energy
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                Registration.BLOCKBOOSTER_T3_BE.get(),
                (be, side) -> be.energy
        );
    }
}