package playtime.meter;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import playtime.meter.mixin.StatAccessor;
import playtime.meter.util.JsonUtil;
import playtime.meter.util.LongStatFormatter;
import playtime.meter.util.events.KeyBindingPressUpdate;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Map;
import java.util.Optional;

/**
 * a copy of {@code StatHandler} which saves values as long instead of int to support numbers larger
 * than {@code Integer.MAX_VALUE}
 */
public final class PlaytimeMeter implements ClientTickEvents.EndTick, ClientLifecycleEvents.ClientStopping,
        KeyBindingPressUpdate {
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

    public void setAFKTimeout(int timeout) {
        aFKTimeout = MathHelper.clamp(timeout, 0, 6000);
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
            Optional<JsonObject> json = JsonUtil.parseFileToObject(Main.MOD_DIRECTORY.resolve("save.json"));

            if (json.isEmpty()) {
                Main.LOGGER.error("The save file is not a json object.");
            } else {
                try {
                    for (Map.Entry<String, JsonElement> entry : json.get().entrySet()) {
                        playtimeMap.put(ClientStats.PLAYTIME.getOrCreateStat(new Identifier(entry.getKey())),
                                JsonUtil.getAsLong(entry.getValue()).orElse(0));
                    }
                } catch (InvalidIdentifierException e) {
                    e.printStackTrace();
                }

                setAFKTimeout(JsonUtil.getAsInt(json.get().get("afk_timeout")).orElse(600));
            }
        } catch (NoSuchFileException ignored) {
            // Seems it's the first time loading the mod. We'll just generate the file for next time.
        } catch (IOException e) {
            Main.LOGGER.error("Couldn't load playtime stats due to the following error:");
            e.printStackTrace();
        }

        save();
    }

    public void save() {
        JsonObject json = new JsonObject();

        for (Object2LongMap.Entry<Stat<Identifier>> entry : playtimeMap.object2LongEntrySet()) {
            json.addProperty(entry.getKey().getValue().toString(), entry.getLongValue());
        }

        json.addProperty("afk_timeout", getAFKTimeout());

        try {
            JsonUtil.saveToFile(json, Main.MOD_DIRECTORY.resolve("save.json"));
        } catch (IOException e) {
            Main.LOGGER.error("Couldn't save playtime stats due to the following error:");
            e.printStackTrace();
        }
    }

    @Override
    public void onEndTick(MinecraftClient minecraftClient) {
        increaseStat(minecraftClient.player, ClientStats.PLAYTIME.getOrCreateStat(ClientStats.TOTAL), 1);

        if (minecraftClient.player == null || (minecraftClient.currentScreen != null
                && minecraftClient.currentScreen.isPauseScreen())) {
            increaseStat(null, ClientStats.PLAYTIME.getOrCreateStat(ClientStats.SCREEN_TIME), 1);
        } else if (getAFKTimeout() > 0 && afkTicks >= getAFKTimeout()) {
            increaseStat(minecraftClient.player, ClientStats.PLAYTIME.getOrCreateStat(ClientStats.AFK), 1);
        } else {
            increaseStat(minecraftClient.player, ClientStats.PLAYTIME.getOrCreateStat(ClientStats.ACTIVE), 1);
            afkTicks++;
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
}
