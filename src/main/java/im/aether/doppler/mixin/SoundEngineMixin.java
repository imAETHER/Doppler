package im.aether.doppler.mixin;

import com.google.common.collect.Multimap;
import im.aether.doppler.SoundData;
import im.aether.doppler.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {

    @Shadow
    @Final
    private Multimap<SoundSource, SoundInstance> instanceBySource;
    @Shadow
    @Final
    private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

    @Shadow
    private boolean loaded;

    @Inject(method = /*? <=1.21.5 {*/ /*"tickNonPaused" *//*?} else {*/ "tickInGameSound" /*?}*/, at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 0))
    private void onTickSounds(CallbackInfo ci) {
        if (loaded) {
            AL11.alSpeedOfSound(Util.SOUND_SPEED);
            AL10.alDopplerFactor(Util.EFFECT_SCALE);
        }

        this.instanceBySource.forEach((src, instance) -> {
            if (instance == null || instance.isRelative() || Util.EFFECT_SCALE == 0) return;

            final ChannelAccess.ChannelHandle channelHandle = this.instanceToChannel.get(instance);
            if (channelHandle != null) {
                channelHandle.execute(ch -> {
                    final int sourceAL = ((ChannelAccessor) ch).getSourceAL();

                    final SoundData srcData = Util.updateSound(
                            instance.hashCode(),
                            new Vec3(instance.getX(), instance.getY(), instance.getZ())
                    );

                    AL10.alSource3f(sourceAL, AL10.AL_VELOCITY,
                            (float) srcData.velocity.x(), (float) srcData.velocity.y(), (float) srcData.velocity.z());

                    final Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
                    if (cameraEntity == null) return;

                    final Vec3 dir = cameraEntity.getEyePosition()
                            .subtract(cameraEntity.xOld,
                                    cameraEntity.yOld + cameraEntity.getEyeHeight(),
                                    cameraEntity.zOld
                            );

                    AL10.alListener3f(AL10.AL_VELOCITY, (float) dir.x(), (float) dir.y(), (float) dir.z());
                });
            }
        });
    }
}
