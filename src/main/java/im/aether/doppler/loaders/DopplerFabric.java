//? if fabric {
package im.aether.doppler.loaders;

import im.aether.doppler.Util;
import im.aether.doppler.addon.PlasmoVC;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DopplerFabric implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Doppler");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing!!");
        Util.loadConfig();

        if (FabricLoader.getInstance().isModLoaded("plasmovoice")) {
            su.plo.voice.api.client.PlasmoVoiceClient.getAddonsLoader().load(new PlasmoVC());
        }

        LOGGER.info("Client initialized!");
    }
}
//?}
