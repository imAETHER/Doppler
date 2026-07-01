package im.aether.doppler.addon;

import im.aether.doppler.SoundData;
import im.aether.doppler.Util;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import su.plo.voice.api.addon.AddonInitializer;
import su.plo.voice.api.addon.annotation.Addon;
import su.plo.voice.api.client.audio.device.source.AlSource;
import su.plo.voice.api.client.event.audio.source.AudioSourceWriteEvent;
import su.plo.voice.api.event.EventSubscribe;

import java.util.UUID;

@Addon(
        id = "pv-addon-doppler",
        name = "Doppler",
        version = /*$ mod_version*/"1.2.3",
        authors = {"imAETHER"}
)
public class PlasmoVC implements AddonInitializer {

    private final Logger logger = LoggerFactory.getLogger("Doppler(PlasmoVC)");

    @Override
    public void onAddonInitialize() {
        logger.info("pv-addon-doppler initialized");
    }

    @EventSubscribe
    public void onSourceWrite(final AudioSourceWriteEvent srcEvent) {
        if (!srcEvent.getSource().canHear()) return;

        final AlSource alSource = srcEvent.getSource().getSource();

        alSource.getDevice().runInContextAsync(() -> {
            final float[] srcPos = new float[3];
            alSource.getFloatArray(AL11.AL_POSITION, srcPos);

            final UUID uuid = srcEvent.getSource().getSourceInfo().getId();

            final SoundData srcData = Util.updateSound(
                    uuid.hashCode(),
                    new Vec3(srcPos[0], srcPos[1], srcPos[2])
            );

            alSource.setFloatArray(AL11.AL_VELOCITY,
                    new float[]{
                            (float) srcData.velocity.x(),
                            (float) srcData.velocity.y(),
                            (float) srcData.velocity.z()
                    }
            );
        });
    }

}
