package im.aether.doppler.mixin;

import com.google.common.collect.Multimap;
import com.mojang.blaze3d.audio.Channel;
import im.aether.doppler.SoundData;
import im.aether.doppler.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.WeakHashMap;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {

    @Shadow
    @Final
    private Multimap<SoundSource, SoundInstance> instanceBySource;

    @Shadow
    protected abstract float calculatePitch(SoundInstance soundInstance);

    @Shadow
    @Final
    private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

    @Unique
    private final Map<SoundInstance, SoundData> soundMotionMap = new WeakHashMap<>();


    @Unique
    private SoundData updateSound(SoundInstance sound) {
        final SoundData sm = soundMotionMap.computeIfAbsent(sound, (k) ->
                new SoundData(new Vec3(sound.getX(), sound.getY(), sound.getZ()), Vec3.ZERO));

        final Vec3 lerp = Util.getSourceLerp(sm, sound);

        sm.velocity = lerp.subtract(sm.lastPos);
        sm.lastPos = lerp;
        return sm;
    }

    @Redirect(method = /*? fabric {*/"method_19748" /*?} else {*/ /*"lambda$tickNonPaused$4(FFLnet/minecraft/world/phys/Vec3;Lcom/mojang/blaze3d/audio/Channel;)V" *//*?}*/,
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/audio/Channel;setPitch(F)V")
    )
    private static void pitchHook(Channel instance, float pitch) {
        if (Util.EFFECT_SCALE == 0) instance.setPitch(pitch);
    }

    @Inject(method = /*? <=1.21.5 {*/ /*"tickNonPaused" *//*?} else {*/ "tickInGameSound" /*?}*/, at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 0))
    private void onTickSounds(CallbackInfo ci) {
        this.instanceBySource.forEach((src, instance) -> {
            if (instance == null) return;

            final ChannelAccess.ChannelHandle channelHandle = this.instanceToChannel.get(instance);
            if (channelHandle != null) {
                channelHandle.execute(channel -> channel.setPitch(calcDoppler(instance)));
            }
        });
    }

    @Unique
    private float calcDoppler(SoundInstance sound) {
        final Minecraft mc = Minecraft.getInstance();

        final Entity cameraEntity = mc.getCameraEntity();
        if (cameraEntity == null) return calculatePitch(sound);

        final SoundData soundData = updateSound(sound);
        final double doppler = Util.doppler(soundData, cameraEntity);

        return (float) Mth.clamp(sound.getPitch() * doppler, 0.5, 2.0);
    }
}
