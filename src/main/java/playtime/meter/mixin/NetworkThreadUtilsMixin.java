package playtime.meter.mixin;

import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import playtime.meter.mixinterfaces.IServerPlayNetworkHandler;

@Mixin(NetworkThreadUtils.class)
public abstract class NetworkThreadUtilsMixin {
    @Inject(at = @At("RETURN"), method = "forceMainThread")
    @SuppressWarnings("unchecked")
    private static <T extends PacketListener> void onForceMainThread(Packet<T> packet, T listener, ServerWorld world,
                                                                     CallbackInfo info) {
        try {
            if (listener instanceof IServerPlayNetworkHandler) {
                ((IServerPlayNetworkHandler) listener).applyReceivedPacket((Packet<ServerPlayPacketListener>) packet, info);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
