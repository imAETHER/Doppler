package im.aether.doppler;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Util {

    public static final float SOUND_SPEED = 17.15f; // 343(irl) / 20(ticks)
    private static final Logger LOGGER = LoggerFactory.getLogger("DopplerUtil");
    private static final File CONFIG = new File(Minecraft.getInstance().gameDirectory + File.separator + "config" + File.separator + "doppler.cfg");
    private static final Cache<Integer, SoundData> SOUND_DATA_MAP = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build();
    public static float EFFECT_SCALE = 0.4f;

    public static void loadConfig() {
        if (!CONFIG.exists()) return;

        try {
            List<String> strings = FileUtils.readLines(CONFIG, StandardCharsets.UTF_8);
            if (strings.isEmpty()) return;

            EFFECT_SCALE = Float.parseFloat(strings.get(0));
            LOGGER.info("Config loaded!");
        } catch (IOException | NumberFormatException e) {
            LOGGER.error("Failed to parse config file!!", e);
        }
    }

    public static void saveConfig() {
        if (!CONFIG.exists()) {
            try {
                CONFIG.createNewFile();
            } catch (IOException e) {
                LOGGER.error("Failed to create config file :/", e);
            }
        }

        try {
            FileUtils.write(CONFIG, String.valueOf(EFFECT_SCALE), StandardCharsets.UTF_8, false);
            LOGGER.info("Config saved!");
        } catch (IOException e) {
            LOGGER.error("Failed to write to config file :/", e);
        }
    }

    public static float getDeltaTime() {
        final Minecraft mc = Minecraft.getInstance();
        return /*? < 1.21.1 {*/ /*mc.getFrameTime() *//*?} else {*/  /*? >= 1.21.5 {*/ mc.getDeltaTracker().getRealtimeDeltaTicks() /*?} else {*/  /*mc.getTimer().getRealtimeDeltaTicks()  *//*?}*/  /*?}*/;
    }

    public static SoundData updateSound(final int id, final Vec3 pos) {
        SoundData sm = SOUND_DATA_MAP.getIfPresent(id);
        if (sm == null) {
            SOUND_DATA_MAP.put(id, sm = new SoundData(pos, Vec3.ZERO));
        }

        final Vec3 lerp = lerpXYZ(sm, pos.x(), pos.y(), pos.z());

        sm.velocity = lerp.subtract(sm.lastPos);
        sm.lastPos = lerp;

        return sm;
    }

    private static Vec3 lerpXYZ(final SoundData sm, final double x, final double y, final double z) {
        final float delta = getDeltaTime();
        return new Vec3(
                Mth.lerp(delta, sm.lastPos.x(), x),
                Mth.lerp(delta, sm.lastPos.y(), y),
                Mth.lerp(delta, sm.lastPos.z(), z)
        );
    }

}
