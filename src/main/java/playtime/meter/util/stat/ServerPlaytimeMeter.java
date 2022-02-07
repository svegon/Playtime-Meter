package playtime.meter.util.stat;

import net.minecraft.item.BlockItem;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import playtime.meter.PlaytimeStats;
import playtime.meter.ServerMain;
import playtime.meter.mixin.ServerPlayerInteractionManagerAccessor;
import playtime.meter.mixinterfaces.IServerPlayNetworkHandler;
import playtime.meter.mixinterfaces.IServerPlayerEntity;
import playtime.meter.util.events.network.C2SPlayPacketListener;

import java.nio.file.Path;

public final class ServerPlaytimeMeter extends PlaytimeMeter implements C2SPlayPacketListener {
    private final ServerPlayerEntity player;
    private int buildingEndTime;

    public ServerPlaytimeMeter(Path saveFile, ServerPlayerEntity player) {
        super(saveFile);
        this.player = player;

        ((IServerPlayerEntity) player).packetReceivedEvent().register(this);
    }

    public void tick() {
        increaseStat(PlaytimeStats.TOTAL, 1);

        if (getAFKTimeout() > 0 && afkTicks >= getAFKTimeout()) {
            increaseStat(PlaytimeStats.AFK, 1);
        } else {
            afkTicks++;
            increaseStat(PlaytimeStats.ACTIVE, 1);

            Vec3d realVelocity = player.getPos().subtract(((IServerPlayNetworkHandler) player.networkHandler)
                    .lastTickPos());
            Vec3d clientVelocity = realVelocity.subtract(player.getVelocity());

            if (Math.abs(clientVelocity.getX()) > 1.0E-4 || Math.abs(clientVelocity.getZ()) > 1.0E-4) {
                increaseStat(PlaytimeStats.WALKING_TIME, 1);
            } else {
                increaseStat(PlaytimeStats.STANDING_TIME, 1);
            }

            if (realVelocity.getY() < 0) {
                increaseStat(PlaytimeStats.FALLING_TIME, 1);
            }

            if (((ServerPlayerInteractionManagerAccessor) player.interactionManager).isMining()) {
                increaseStat(PlaytimeStats.MINING_TIME, 1);
            }

            if (player.currentScreenHandler instanceof CraftingScreenHandler
                    || (player.currentScreenHandler == player.playerScreenHandler
                    && isCrafting(player.playerScreenHandler))) {
                increaseStat(PlaytimeStats.CRAFTING_TIME, 1);
            }

            if (player.getAttackCooldownProgress(0) < 1) {
                increaseStat(PlaytimeStats.FIGHTING_TIME, 1);
            }

            if (player.age <= buildingEndTime) {
                increaseStat(PlaytimeStats.BUILDING_TIME, 1);
            }
        }
    }

    @Override
    public void apply(Packet<ServerPlayPacketListener> packet, CallbackInfo info) {
        if (!(packet instanceof KeepAliveC2SPacket || packet instanceof CustomPayloadC2SPacket)) {
            afkTicks = 0;
        }

        C2SPlayPacketListener.super.apply(packet, info);
    }

    @Override
    public void onPlayerInteractBlock(@NotNull PlayerInteractBlockC2SPacket packet) {
        if (!(player.getStackInHand(packet.getHand()).getItem() instanceof BlockItem)) {
            return;
        }

        BlockPos blockPos = packet.getBlockHitResult().getBlockPos();

        if (blockPos.getY() < this.player.world.getTopY() && this.player.squaredDistanceTo((double)blockPos.getX()
                + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) < 64.0
                && player.getWorld().canPlayerModifyAt(this.player, blockPos)) {
            buildingEndTime = player.age + 4;
        }
    }

    @Override
    public int getAFKTimeout() {
        return ServerMain.getSettings().getAFKTimeout();
    }
}
