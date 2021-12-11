package playtime.meter.util.stat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import playtime.meter.ClientStats;
import playtime.meter.Main;
import playtime.meter.util.JsonUtil;
import playtime.meter.util.StatParser;
import playtime.meter.util.events.KeyBindingPressUpdate;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public final class ClientPlaytimeMeter extends PlaytimeMeter
        implements ClientTickEvents.EndTick, KeyBindingPressUpdate {
    private final Object2LongMap<Stat<Identifier>> playtimeMap =
            Object2LongMaps.synchronize(new Object2LongOpenHashMap<>());

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

        increaseStat(player, ClientStats.TOTAL, 1);

        if (player == null || (minecraftClient.currentScreen != null
                && minecraftClient.currentScreen.isPauseScreen())) {
            increaseStat(null, ClientStats.SCREEN_TIME, 1);
        } else if (getAFKTimeout(MinecraftClient.getInstance().player) > 0 && afkTicks >= getAFKTimeout(player)) {
            increaseStat(player, ClientStats.AFK, 1);
        } else {
            increaseStat(player, ClientStats.ACTIVE, 1);
            afkTicks++;
        }
    }

    @Override
    public void onKeyBindingPressUpdate(KeyBinding keyBinding, boolean pressed, CallbackInfo info) {
        afkTicks = 0;
    }

    public static int getDefaultAFKTimeout() {
        return 600;
    }
}
