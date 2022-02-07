package playtime.meter.mixin;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import playtime.meter.mixinterfaces.IServerPlayerEntity;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Inject(at = {@At("RETURN")}, method = "savePlayerData")
    private void onSavePlayerData(ServerPlayerEntity player, CallbackInfo info) {
        ((IServerPlayerEntity) player).getPlaytimeMeter().save();
    }

    @Inject(at = @At("HEAD"), method = "respawnPlayer")
    private void onRespawnPlayer(ServerPlayerEntity player, boolean alive,
                                 CallbackInfoReturnable<ServerPlayerEntity> info) {
        ((IServerPlayerEntity) player).getPlaytimeMeter().save();
    }
}
