package playtime.meter;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import playtime.meter.mixin.StatAccessor;
import playtime.meter.util.JsonUtil;
import playtime.meter.util.LongStatFormatter;
import playtime.meter.util.OptionalUtil;
import playtime.meter.util.events.KeyBindingPressUpdate;
import playtime.meter.util.events.ScreenKeyPressEvent;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/**
 * a copy of {@code StatHandler} which saves values as long instead of int to support numbers larger
 * than {@code Integer.MAX_VALUE}
 */
public final class PlaytimeMeter implements ClientTickEvents.EndTick, ClientLifecycleEvents.ClientStopping,
        KeyBindingPressUpdate, ScreenKeyPressEvent {
    public static final Logger LOGGER = LogManager.getLogger("playtime-meter");
    public static final Path MOD_DIRECTORY = FabricLoader.getInstance().getGameDir().resolve("Playtime Meter");
    static PlaytimeMeter playtimeMeter;

    private int aFKTimeout = 600;

    private final Object2LongMap<Stat<Identifier>> playtimeMap =
            Object2LongMaps.synchronize(new Object2LongOpenHashMap<>());
    private final Map<Stat<Identifier>, LongStatFormatter> formatterMap = Maps.newHashMap();
    private int afkTicks;

    public PlaytimeMeter() {
        playtimeMap.defaultReturnValue(0);
    }

    public void increaseStat(@Nullable PlayerEntity player, Stat<Identifier> stat, long value) {
        this.setStat(player, stat, getStat(stat) + value);
    }

    public void setStat(@Nullable PlayerEntity player, Stat<Identifier> stat, long value) {
        playtimeMap.put(stat, value);
/*
        if (player instanceof ClientPlayerEntity) {
            ((ClientPlayerEntity) player).getStatHandler().setStat(player, stat, (int) Math.min(value, 2147483647L));
        }*/
    }

    public long getStat(@NotNull StatType<Identifier> type, Identifier stat) {
        return type.hasStat(stat) ? this.getStat(type.getOrCreateStat(stat)) : 0;
    }

    public long getStat(Stat<Identifier> stat) {
        return playtimeMap.getLong(stat);
    }

    public int setAFKTimeout(int timeout) {
        return aFKTimeout = Math.max(timeout, 0);
    }

    public int getAFKTimeout() {
        return aFKTimeout;
    }

    public String format(Stat<Identifier> stat) {
        return formatterMap.computeIfAbsent(stat, (s) -> LongStatFormatter.fromNormal(((StatAccessor) s)
                .getFormatter())).format(getStat(stat));
    }

    public void load() {
        try {
            Optional<JsonObject> optional = JsonUtil.parseFileToObject(MOD_DIRECTORY.resolve("playtimes.json"));

            if (optional.isEmpty()) {
                LOGGER.error("The playtimes file is not a json object.");
            } else {
                JsonObject json = optional.get();

                for (Identifier stat : ClientStats.PLAYTIME_STATS) {
                    playtimeMap.put(ClientStats.PLAYTIME.getOrCreateStat(stat), OptionalUtil.flatMapToInt(
                            JsonUtil.getProperty(json, stat.toString()), JsonUtil::getAsInt).orElse(0));
                }
            }

            Optional<JsonObject> optionalSettings =
                    JsonUtil.parseFileToObject(MOD_DIRECTORY.resolve("settings.json"));

            if (optionalSettings.isPresent()) {
                JsonObject settingsJson = optionalSettings.get();

                setAFKTimeout(OptionalUtil.flatMapToInt(JsonUtil.getProperty(settingsJson, "AFK_timeout"),
                        JsonUtil::getAsInt).orElse(getAFKTimeout()));
            } else {
                LOGGER.error("The settings file is not a json object.");
            }
        } catch (NoSuchFileException ignored) {
            // Seems it's the first time loading the mod. We'll just generate the file for next time.
        } catch (IOException | JsonParseException e) {
            LOGGER.error("Couldn't load playtime stats due to the following error:");
            e.printStackTrace();
        }

        save();
    }

    public void save() {
        JsonObject settings = new JsonObject();
        JsonObject playtimes = new JsonObject();

        for (Object2LongMap.Entry<Stat<Identifier>> entry : playtimeMap.object2LongEntrySet()) {
            playtimes.addProperty(entry.getKey().getValue().toString(), entry.getLongValue());
        }

        settings.addProperty("AFK_timeout", getAFKTimeout());

        try {
            JsonUtil.saveToFile(playtimes, MOD_DIRECTORY.resolve("playtimes.json"));
            JsonUtil.saveToFile(playtimes, MOD_DIRECTORY.resolve("settings.json"));
        } catch (IOException e) {
            LOGGER.error("Couldn't save playtime stats due to the following error:");
            e.printStackTrace();
        }
    }

    @Override
    public void onEndTick(MinecraftClient minecraftClient) {
        increaseStat(minecraftClient.player, ClientStats.PLAYTIME.getOrCreateStat(ClientStats.TOTAL), 1);

        if (minecraftClient.player == null || (minecraftClient.currentScreen != null
                && minecraftClient.currentScreen.isPauseScreen() && minecraftClient.isInSingleplayer())) {
            increaseStat(minecraftClient.player, ClientStats.PLAYTIME.getOrCreateStat(ClientStats.SCREEN_TIME),
                    1);
        } else if (getAFKTimeout() > 0) {
            if (afkTicks >= getAFKTimeout()) {
                increaseStat(minecraftClient.player, ClientStats.PLAYTIME.getOrCreateStat(ClientStats.AFK), 1);
            } else {
                increaseStat(minecraftClient.player, ClientStats.PLAYTIME.getOrCreateStat(ClientStats.ACTIVE), 1);
                afkTicks++;
            }
        } else {
            increaseStat(minecraftClient.player, ClientStats.PLAYTIME.getOrCreateStat(ClientStats.ACTIVE), 1);
        }
    }

    @Override
    public void onClientStopping(MinecraftClient minecraftClient) {
        save();
    }

    @Override
    public void onKeyBindingPressUpdate(KeyBinding keyBinding, boolean pressed, CallbackInfo info) {
        afkTicks = 0;
    }

    @Override
    public void onScreenKeyPress(int key, int scanCode, int modifiers, boolean action) {
        if (action) {
            afkTicks = 0;
        }
    }

    public static PlaytimeMeter getInstance() {
        return playtimeMeter;
    }
}
