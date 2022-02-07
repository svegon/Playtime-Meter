package playtime.meter.mixin;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import playtime.meter.Main;
import playtime.meter.mixinterfaces.IServerPlayerEntity;
import playtime.meter.util.events.network.C2SPlayPacketListener;
import playtime.meter.util.events.network.S2CPlayPacketListener;
import playtime.meter.util.stat.ServerPlaytimeMeter;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements IServerPlayerEntity {
    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Unique
    private @NotNull ServerPlaytimeMeter playtimeMeter;
    @Unique
    private Event<C2SPlayPacketListener> packetReceivedEvent;
    @Unique
    private Event<S2CPlayPacketListener> packetSentEvent;

    @Inject(at = {@At("RETURN")}, method = {"<init>"})
    @SuppressWarnings("unchecked")
    private void init(MinecraftServer server, ServerWorld world, GameProfile profile, CallbackInfo info) {
        packetSentEvent =
                EventFactory.createArrayBacked(S2CPlayPacketListener.class,
                        listeners -> new S2CPlayPacketListener() {
                            @Override
                            public void apply(Packet<ClientPlayPacketListener> packet, CallbackInfo info) {
                                for (S2CPlayPacketListener listener : listeners) {
                                    listener.apply(packet, info);

                                    if (info.isCancelled()) {
                                        return;
                                    }
                                }
                            }
                        });
        packetReceivedEvent =
                EventFactory.createArrayBacked(C2SPlayPacketListener.class,
                        listeners -> new C2SPlayPacketListener() {
                            @Override
                            public void apply(Packet<ServerPlayPacketListener> packet, CallbackInfo info) {
                                for (C2SPlayPacketListener listener : listeners) {
                                    listener.apply(packet, info);

                                    if (info.isCancelled()) {
                                        return;
                                    }
                                }
                            }
                        });
        playtimeMeter = new ServerPlaytimeMeter(Main.MOD_DIRECTORY.resolve("playtimes").resolve(getUuidAsString()
                + ".json"), (ServerPlayerEntity) (Object) this);
    }

    @Inject(at = {@At("RETURN")}, method = {"tick"})
    private void onTick(CallbackInfo info) {
        playtimeMeter.tick();
    }

    @Override
    public @NotNull ServerPlaytimeMeter getPlaytimeMeter() {
        return playtimeMeter;
    }

    @Override
    public Event<C2SPlayPacketListener> packetReceivedEvent() {
        return packetReceivedEvent;
    }

    @Override
    public Event<S2CPlayPacketListener> packetSentEvent() {
        return packetSentEvent;
    }
}
