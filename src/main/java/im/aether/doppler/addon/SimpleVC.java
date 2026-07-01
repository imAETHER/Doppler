package im.aether.doppler.addon;

/*? if forge || neoforge {*/
/*import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
*//*?}*/
import de.maxhenkel.voicechat.api.Position;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.OpenALSoundEvent;
import im.aether.doppler.SoundData;
import im.aether.doppler.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/*? if forge || neoforge {*/
/*@ForgeVoicechatPlugin
*//*?}*/
public class SimpleVC implements VoicechatPlugin {

    private final Logger logger = LoggerFactory.getLogger("Doppler(SimpleVC)");

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

    private void onALSound(final OpenALSoundEvent sev) {
        final Minecraft mc = Minecraft.getInstance();

        final Entity cameraEntity = mc.getCameraEntity();
        if (cameraEntity == null) return;

        final Position position = sev.getPosition();
        if (position == null) return;

        final UUID channelId = sev.getChannelId();
        if (channelId == null) return;

        final SoundData srcData = Util.updateSound(channelId.hashCode(),
                new Vec3(position.getX(), position.getY(), position.getZ()));

        AL11.alSource3f(sev.getSource(),
                AL11.AL_VELOCITY,
                (float) srcData.velocity.x(),
                (float) srcData.velocity.y(),
                (float) srcData.velocity.z()
        );
    }
}
