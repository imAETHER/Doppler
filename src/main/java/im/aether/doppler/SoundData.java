package im.aether.doppler;

import net.minecraft.world.phys.Vec3;

public class SoundData {

    public Vec3 lastPos;
    public Vec3 velocity;

    public SoundData(final Vec3 current, final Vec3 velocity) {
        this.lastPos = current;
        this.velocity = velocity;
    }
}
