package playtime.meter.util.stat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.realms.gui.screen.RealmsDownloadLatestWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsUploadScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import playtime.meter.ClientStats;
import playtime.meter.Main;
import playtime.meter.mixin.ClientPlayerInteractionManagerAccessor;
import playtime.meter.mixin.MinecraftClientAccessor;
import playtime.meter.util.JsonUtil;
import playtime.meter.util.StatParser;
import playtime.meter.util.events.KeyBindingPressUpdate;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public final class ClientPlaytimeMeter extends PlaytimeMeter
        implements ClientTickEvents.EndTick, KeyBindingPressUpdate {
    private final Object2LongMap<Stat<Identifier>> playtimeMap = new Object2LongOpenHashMap<>();

    private int aFKTimeout = getDefaultAFKTimeout();
    private int afkTicks;

    public ClientPlaytimeMeter(Path saveFolder) {
        super(saveFolder);
        playtimeMap.defaultReturnValue(0);
    }

    @Override
    public synchronized void increaseStat(PlayerEntity player, Stat<Identifier> stat, long value) {
        super.increaseStat(player, stat, value);
    }

    @Override
    public synchronized void setStat(PlayerEntity player, Stat<Identifier> stat, long value) {
        playtimeMap.put(stat, value);
    }

    @Override
    public synchronized long getStat(PlayerEntity player, @NotNull StatType<Identifier> type, Identifier stat) {
        return super.getStat(player, type, stat);
    }

    @Override
    public synchronized long getStat(PlayerEntity player, Stat<Identifier> stat) {
        return playtimeMap.getLong(stat);
    }

    @Override
    public synchronized int setAFKTimeout(PlayerEntity player, int timeout) {
        int ret = aFKTimeout;
        aFKTimeout = MathHelper.clamp(timeout, 0, 6000);
        return ret;
    }

    @Override
    public int getAFKTimeout(PlayerEntity player) {
        return aFKTimeout;
    }

    @Override
    public void load() {
        playtimeMap.putAll(StatParser.parsePlaytimesFileClient(getSaveFolder()
                .resolve("playtimes.json"), (e) -> {
            if (e instanceof IOException) {
                if (!(e instanceof NoSuchFileException)) {
                    Main.LOGGER.error("Couldn't load playtime stats due to the following error:");
                    e.printStackTrace();
                }
            } else if (e instanceof JsonParseException) {
                Main.LOGGER.error("The playtimes save file is not a json object.");
            } else if (e instanceof InvalidIdentifierException) {
                Main.LOGGER.error(e.getMessage());
            }
        }));

        try {
            Optional<JsonObject> options = JsonUtil.parseFileToObject(getSaveFolder().resolve("options.json"));

            if (options.isEmpty()) {
                Main.LOGGER.error("The options save file is not a json object.");
            } else {
                setAFKTimeout(MinecraftClient.getInstance().player,
                        JsonUtil.getAsInt(options.get().get("afk_timeout"))
                                .orElseGet(ClientPlaytimeMeter::getDefaultAFKTimeout));
            }
        } catch (NoSuchFileException ignored) {
            // same case as before
        } catch (JsonParseException | IOException e) {
            Main.LOGGER.error("Couldn't load options due to the following error:");
            e.printStackTrace();
        }

        save();
    }

    @Override
    public void save() {
        JsonObject playtimes = StatParser.parsePlaytimesJsonClient(playtimeMap);
        JsonObject options = new JsonObject();

        options.addProperty("afk_timeout", getAFKTimeout(MinecraftClient.getInstance().player));

        try {
            JsonUtil.saveToFile(playtimes, getSaveFolder().resolve("playtimes.json"));
            JsonUtil.saveToFile(options, getSaveFolder().resolve("options.json"));
        } catch (IOException e) {
            Main.LOGGER.error("Couldn't save playtime stats due to the following error:");
            e.printStackTrace();
        }
    }

    @Override
    public void onEndTick(MinecraftClient minecraftClient) {
        ClientPlayerEntity player = minecraftClient.player;
        Screen screen = minecraftClient.currentScreen;

        increaseStat(player, ClientStats.TOTAL, 1);

        if (player == null || (screen != null
                && screen.isPauseScreen() && minecraftClient.isInSingleplayer())) {
            increaseStat(player, ClientStats.SCREEN_TIME, 1);

            if (screen instanceof TitleScreen) {
                increaseStat(player, ClientStats.TITLE_SCREEN_TIME, 1);
            } else if (screen instanceof LevelLoadingScreen || screen instanceof DownloadingTerrainScreen
                    || screen instanceof ProgressScreen || screen instanceof ConnectScreen
                    || screen instanceof RealmsDownloadLatestWorldScreen || screen instanceof RealmsUploadScreen) {
                increaseStat(player, ClientStats.LOADING_SCREEN_TIME, 1);
            } else if (player == null) {
                increaseStat(null, ClientStats.WORLD_SCREEN_TIME, 1);
            } else {
                increaseStat(player, ClientStats.PAUSE_SCREEN_TIME, 1);
            }
                /* else if (screen instanceof SelectWorldScreen || screen instanceof DirectConnectScreen
                    || screen instanceof PackScreen || screen instanceof OutOfMemoryScreen
                    || screen instanceof PresetsScreen || screen instanceof DisconnectedScreen
                    || screen instanceof OptimizeWorldScreen || screen instanceof EditWorldScreen
                    || screen instanceof NoticeScreen || screen instanceof LevelLoadingScreen
                    || screen instanceof MultiplayerWarningScreen || screen instanceof ProgressScreen
                    || screen instanceof CustomizeBuffetLevelScreen || screen instanceof EditGameRulesScreen
                    || screen instanceof AddServerScreen || screen instanceof DownloadingTerrainScreen) {
                increaseStat(player, ClientStats.WORLD_SCREEN_TIME, 1);
            }*/
        } else if (getAFKTimeout(player) > 0 && afkTicks >= getAFKTimeout(player)) {
            increaseStat(player, ClientStats.AFK, 1);
        } else {
            afkTicks++;
            increaseStat(player, ClientStats.ACTIVE, 1);

            if (isMovementkeyPressed(minecraftClient)) {
                increaseStat(player, ClientStats.WALKING_TIME, 1);
            } else {
                increaseStat(player, ClientStats.STANDING_TIME, 1);
            }

            if (player.getVelocity().getY() < 0) {
                increaseStat(player, ClientStats.FALLING_TIME, 1);
            }

            if (((ClientPlayerInteractionManagerAccessor) minecraftClient.interactionManager)
                    .getCurrentBreakingProgress() == 0) {
                increaseStat(player, ClientStats.MINING_TIME, 1);
            }

            if (((MinecraftClientAccessor) minecraftClient).getItemUseCooldown() > 0) {
                increaseStat(player, ClientStats.BUILDING_TIME, 1);
            }

            if (screen instanceof CraftingScreen || (screen instanceof InventoryScreen
                    && isCrafting(player.playerScreenHandler))) {
                increaseStat(player, ClientStats.CRAFTING_TIME, 1);
            }

            if (player.getAttackCooldownProgress(0) < 1) {
                increaseStat(player, ClientStats.FIGHTING_TIME, 1);
            }
        }
    }

    @Override
    public void onKeyBindingPressUpdate(KeyBinding keyBinding, boolean pressed, CallbackInfo info) {
        afkTicks = 0;
    }

    public static int getDefaultAFKTimeout() {
        return 600;
    }

    private static boolean isMovementkeyPressed(MinecraftClient client) {
        GameOptions options = client.options;

        return Stream.of(options.keyForward, options.keyBack, options.keyLeft, options.keyRight, options.keyJump)
                .anyMatch(KeyBinding::isPressed);
    }

    private static boolean isCrafting(PlayerScreenHandler handler) {
        for (int i = 1; i < 5; i++) {
            if (!handler.slots.get(i).getStack().isEmpty()) {
                return true;
            }
        }

        return false;
    }
}
