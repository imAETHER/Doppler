package im.aether.doppler.addon;

/*? if forge || neoforge {*/
import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
/*?}*/
import de.maxhenkel.voicechat.api.Position;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.OpenALSoundEvent;
import im.aether.doppler.SoundData;
import im.aether.doppler.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.WeakHashMap;

/*? if forge || neoforge {*/
@ForgeVoicechatPlugin
/*?}*/
public class SimpleVC implements VoicechatPlugin {

    private final Logger logger = LoggerFactory.getLogger("Doppler(SimpleVC)");
    private final Map<Integer, SoundData> soundMotionMap = new WeakHashMap<>();

    @Override
    public String getPluginId() {
        return "vc-addon-doppler";
    }

    @Override
    public void initialize(VoicechatApi api) {
        logger.info("SimpleVC-API initialized");
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(OpenALSoundEvent.class, this::onALSound);
    }

    // The reason this is here is bc idk how java will react to having missing types in the Util class
    private Vec3 lerpPosition(final SoundData sm, final Position pos) {
        final float delta = Util.getDeltaTime();
        return new Vec3(
                Mth.lerp(delta, sm.lastPos.x(), pos.getX()),
                Mth.lerp(delta, sm.lastPos.y(), pos.getY()),
                Mth.lerp(delta, sm.lastPos.z(), pos.getZ())
        );
    }

    private SoundData updateSound(final OpenALSoundEvent sound) {
        if (sound.getPosition() == null) return null;

        final SoundData sm = soundMotionMap.computeIfAbsent(sound.getSource(), (k) -> {
            final Position pos = sound.getPosition();
            return new SoundData(new Vec3(pos.getX(), pos.getY(), pos.getZ()), Vec3.ZERO);
        });

        final Vec3 lerp = lerpPosition(sm, sound.getPosition());

        sm.velocity = lerp.subtract(sm.lastPos);
        sm.lastPos = lerp;
        return sm;
    }

    private float calcDoppler(OpenALSoundEvent sound) {
        final Minecraft mc = Minecraft.getInstance();

        final Entity cameraEntity = mc.getCameraEntity();
        final SoundData soundData = updateSound(sound);
        if (cameraEntity == null || soundData == null) return 1.0f;

        return (float) Mth.clamp(Util.doppler(soundData, cameraEntity), 0.5, 2.0);
    }

    private void onALSound(final OpenALSoundEvent sev) {
        AL11.alSourcef(sev.getSource(), AL11.AL_PITCH, calcDoppler(sev));
    }
}
