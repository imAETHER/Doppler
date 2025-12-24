//? if forge {
/*package im.aether.doppler.loaders;

import im.aether.doppler.Util;
import net.minecraftforge.common.MinecraftForge;
import im.aether.doppler.addon.PlasmoVC;
import im.aether.doppler.screen.OptionScreen;
/^? < 1.20.1 && != 1.19.2 {^/
/^import net.minecraftforge.client.ConfigGuiHandler;
^//^?}^/
/^? <= 1.20.1 && != 1.19.2 {^/

/^import net.minecraftforge.fml.ModLoadingContext;
^//^?}^/

/^? >= 1.19.2 && <= 1.20.1 {^/
/^import net.minecraftforge.client.ConfigScreenHandler;
^//^?}^/

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("doppler")
public class DopplerForge {
    private static final Logger LOGGER = LoggerFactory.getLogger("Doppler");

    public DopplerForge(/^? >1.18.2 && <1.20.1 {^/ /^final FMLJavaModLoadingContext context ^//^?}^/) {
        LOGGER.info("Initializing!");

        MinecraftForge.EVENT_BUS.addListener((FMLClientSetupEvent event) -> Util.loadConfig());
        /^? if > 1.18.2 && < 1.20.1 {^/
        /^context.getContainer().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, prevScreen)
                        -> new OptionScreen(prevScreen)));
        ^//^?} else {^/
        ModLoadingContext.get().registerExtensionPoint(/^? <1.20.1 {^//^ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory((mc, parent) -> new OptionScreen(parent))^//^?} else {^/
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, prevScreen)
                        -> new OptionScreen(prevScreen))/^?}^/
        );
        /^?}^/
        if (ModList.get().isLoaded("plasmovoice")) {
            su.plo.voice.api.client.PlasmoVoiceClient.getAddonsLoader().load(new PlasmoVC());
        }
        LOGGER.info("Forge client initialized!");
    }
}
*///?}
