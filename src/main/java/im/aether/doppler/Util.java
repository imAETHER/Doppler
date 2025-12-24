package im.aether.doppler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class Util {

    public static final double SOUND_SPEED = 17.15; // 343(irl) / 20(ticks)
    private static final Logger LOGGER = LoggerFactory.getLogger("DopplerUtil");
    private static final File CONFIG = new File(Minecraft.getInstance().gameDirectory + File.separator + "config" + File.separator + "doppler.cfg");
    private static final Map<Integer, SoundData> AL_SOUND_DATA = new WeakHashMap<>();
    public static double EFFECT_SCALE = 0.4;

    public static void loadConfig() {
        if (!CONFIG.exists()) return;

        try {
            List<String> strings = FileUtils.readLines(CONFIG, StandardCharsets.UTF_8);
            if (strings.isEmpty()) return;

            EFFECT_SCALE = Double.parseDouble(strings.get(0));
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
        return /*? < 1.21.1 {*/ mc.getFrameTime() /*?} else {*/  /*/^? >= 1.21.5 {^/ /^mc.getDeltaTracker().getRealtimeDeltaTicks() ^//^?} else {^/  mc.getTimer().getRealtimeDeltaTicks()  /^?}^/  *//*?}*/;
    }

    public static Vec3 getListenerLerp(final Entity entity) {
        final float delta = getDeltaTime();
        return new Vec3(
                Mth.lerp(delta, entity.xOld, entity.getX()),
                Mth.lerp(delta, entity.yOld, entity.getY()),
                Mth.lerp(delta, entity.zOld, entity.getZ())
        );
    }

    public static float fullDopplerAL(final int pointer, final float[] srcPos) {
        final Minecraft mc = Minecraft.getInstance();

        final Entity cameraEntity = mc.getCameraEntity();
        if (cameraEntity == null) return 1.0f;

        final SoundData sm = AL_SOUND_DATA.computeIfAbsent(pointer, (k) ->
                new SoundData(new Vec3(srcPos[0], srcPos[1], srcPos[2]), Vec3.ZERO));

        final Vec3 lerp = Util.lerpXYZ(sm, srcPos[0], srcPos[1], srcPos[2]);

        sm.velocity = lerp.subtract(sm.lastPos);
        sm.lastPos = lerp;

        final double doppler = Util.doppler(sm, cameraEntity);

        return (float) Mth.clamp(doppler, 0.5, 2.0);
    }

    public static Vec3 getSourceLerp(final SoundData sm, final SoundInstance sound) {
        return lerpXYZ(sm, sound.getX(), sound.getY(), sound.getZ());
    }

    public static Vec3 lerpXYZ(final SoundData sm, final double x, final double y, final double z) {
        final float delta = getDeltaTime();
        return new Vec3(
                Mth.lerp(delta, sm.lastPos.x(), x),
                Mth.lerp(delta, sm.lastPos.y(), y),
                Mth.lerp(delta, sm.lastPos.z(), z)
        );
    }

    // here be the juice
    public static double doppler(final SoundData soundData, final Entity cameraEntity) {
        final Vec3 dir = soundData.lastPos
                .subtract(Util.getListenerLerp(cameraEntity)).normalize();

        final Vec3 lstVelo = cameraEntity.getDeltaMovement();
        return (SOUND_SPEED + lstVelo.dot(dir) * EFFECT_SCALE)
                / (SOUND_SPEED + soundData.velocity.dot(dir) * EFFECT_SCALE);
    }

}
