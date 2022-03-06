package playtime.meter.mixinterfaces;

import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface IServerPlayNetworkHandler {
    void applyReceivedPacket(Packet<ServerPlayPacketListener> packet, CallbackInfo info);

    int ticks();

    Vec3d lastTickPos();
}
