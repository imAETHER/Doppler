//? if neoforge {
/*package im.aether.doppler.loaders;

import im.aether.doppler.Util;
import im.aether.doppler.addon.PlasmoVC;
import im.aether.doppler.screen.OptionScreen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("doppler")
public class DopplerNeo {
    private static final Logger LOGGER = LoggerFactory.getLogger("Doppler");

    public DopplerNeo(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Initializing!");
        modEventBus.addListener((FMLClientSetupEvent event) -> Util.loadConfig());

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (mc, parent) -> new OptionScreen(parent));

        if (ModList.get().isLoaded("plasmovoice")) {
            su.plo.voice.api.client.PlasmoVoiceClient.getAddonsLoader().load(new PlasmoVC());
        }
        LOGGER.info("Client initialized!");
    }
}
*///?}
