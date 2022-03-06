package playtime.meter.util.stat;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.realms.gui.screen.RealmsDownloadLatestWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsUploadScreen;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import playtime.meter.PlaytimeStats;
import playtime.meter.mixin.ClientPlayerInteractionManagerAccessor;
import playtime.meter.mixin.MinecraftClientAccessor;
import playtime.meter.util.events.input.KeyBindingPressUpdate;

import java.nio.file.Path;

public class ClientPlaytimeMeter extends PlaytimeMeter implements ClientTickEvents.EndTick, KeyBindingPressUpdate {
    private int aFKTimeout = getDefaultAFKTimeout();

    public ClientPlaytimeMeter(Path saveFile) {
        super(saveFile);
    }

    public synchronized int setAFKTimeout(int timeout) {
        int ret = aFKTimeout;
        aFKTimeout = MathHelper.clamp(timeout, 0, 6000);
        afkTicks = 0;
        return ret;
    }

    public int getAFKTimeout() {
        return aFKTimeout;
    }

    @Override
    public void onEndTick(MinecraftClient minecraftClient) {
        ClientPlayerEntity player = minecraftClient.player;
        Screen screen = minecraftClient.currentScreen;

        increaseStat(PlaytimeStats.TOTAL, 1);

        if (player == null || (screen != null
                && screen.shouldPause() && minecraftClient.isInSingleplayer())) {
            increaseStat(PlaytimeStats.SCREEN_TIME, 1);

            if (screen instanceof TitleScreen) {
                increaseStat(PlaytimeStats.TITLE_SCREEN_TIME, 1);
            } else if (screen instanceof LevelLoadingScreen || screen instanceof DownloadingTerrainScreen
                    || screen instanceof ProgressScreen || screen instanceof ConnectScreen
                    || screen instanceof RealmsDownloadLatestWorldScreen || screen instanceof RealmsUploadScreen) {
                increaseStat(PlaytimeStats.LOADING_SCREEN_TIME, 1);
            } else if (player == null) {
                increaseStat(PlaytimeStats.WORLD_SCREEN_TIME, 1);
            } else {
                increaseStat(PlaytimeStats.PAUSE_SCREEN_TIME, 1);
            }
        } else if (getAFKTimeout() > 0 && afkTicks >= getAFKTimeout()) {
            increaseStat(PlaytimeStats.AFK, 1);
        } else {
            afkTicks++;
            increaseStat(PlaytimeStats.ACTIVE, 1);

            if (isMovementKeyPressed(minecraftClient.options)) {
                increaseStat(PlaytimeStats.WALKING_TIME, 1);
            } else {
                increaseStat(PlaytimeStats.STANDING_TIME, 1);
            }

            if (player.getVelocity().getY() < 0) {
                increaseStat(PlaytimeStats.FALLING_TIME, 1);
            }

            if (((ClientPlayerInteractionManagerAccessor) minecraftClient.interactionManager)
                    .getCurrentBreakingProgress() == 0) {
                increaseStat(PlaytimeStats.MINING_TIME, 1);
            }

            if (player.getMainHandStack().getItem() instanceof BlockItem
                    && ((MinecraftClientAccessor) minecraftClient).getItemUseCooldown() > 0) {
                increaseStat(PlaytimeStats.BUILDING_TIME, 1);
            }

            if (screen instanceof CraftingScreen || (screen instanceof InventoryScreen
                    && isCrafting(player.playerScreenHandler))) {
                increaseStat(PlaytimeStats.CRAFTING_TIME, 1);
            }

            if (player.getAttackCooldownProgress(0) < 1) {
                increaseStat(PlaytimeStats.FIGHTING_TIME, 1);
            }
        }
    }

    @Override
    public void onKeyBindingPressUpdate(KeyBinding keyBinding, boolean pressed, CallbackInfo info) {
        afkTicks = 0;
    }
}
