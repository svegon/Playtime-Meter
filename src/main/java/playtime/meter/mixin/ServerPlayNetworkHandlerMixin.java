package playtime.meter.mixin;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import playtime.meter.mixinterfaces.IServerPlayNetworkHandler;
import playtime.meter.mixinterfaces.IServerPlayerEntity;
import playtime.meter.util.events.network.C2SPlayPacketListener;
import playtime.meter.util.events.network.S2CPlayPacketListener;
import playtime.meter.util.stat.ServerPlaytimeMeter;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin implements IServerPlayNetworkHandler {
    @Shadow
    public ServerPlayerEntity player;
    @Shadow
    private int ticks;
    @Shadow
    private double lastTickX;
    @Shadow
    private double lastTickY;
    @Shadow
    private double lastTickZ;

    @Inject(at = @At("HEAD"), method = "sendPacket(Lnet/minecraft/network/Packet;" +
            "Lio/netty/util/concurrent/GenericFutureListener;)V")
    @SuppressWarnings("unchecked")
    private void onSendPacket(Packet<?> packet,
                              @Nullable GenericFutureListener<? extends Future<? super Void>> listener,
                              CallbackInfo info) {
        ((IServerPlayerEntity) player).packetSentEvent().invoker().apply((Packet) packet, info);
    }

    @Override
    public int ticks() {
        return ticks;
    }

    @Override
    public Vec3d lastTickPos() {
        return new Vec3d(lastTickX, lastTickY, lastTickZ);
    }
}
