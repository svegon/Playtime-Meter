package playtime.meter.util.stat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
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
import net.minecraft.item.BlockItem;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import playtime.meter.Main;
import playtime.meter.PlaytimeStats;
import playtime.meter.mixin.ClientPlayerInteractionManagerAccessor;
import playtime.meter.mixin.MinecraftClientAccessor;
import playtime.meter.mixin.StatAccessor;
import playtime.meter.util.JsonUtil;
import playtime.meter.util.StatParser;
import playtime.meter.util.events.input.KeyBindingPressUpdate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * a copy of {@code StatHandler} which saves values as long instead of int to support numbers larger
 * than {@code Integer.MAX_VALUE}
 */
public abstract class PlaytimeMeter {
    private Path saveFile;
    private final Object2LongMap<Stat<Identifier>> playtimeMap = new Object2LongOpenHashMap<>();

    protected int afkTicks;

    public PlaytimeMeter(Path saveFile) {
        this.saveFile = saveFile;
        playtimeMap.defaultReturnValue(0);
    }

    public synchronized void increaseStat(Stat<Identifier> stat, long value) {
        setStat(stat, getStat(stat) + value);
    }

    public synchronized void setStat(Stat<Identifier> stat, long value) {
        playtimeMap.put(stat, value);
    }

    public synchronized long getStat(@NotNull StatType<Identifier> type, Identifier stat) {
        stat = PlaytimeStats.PLAYTIME_STATS.get(stat);
        return stat != null ? getStat(type.getOrCreateStat(stat)) : 0;
    }

    public synchronized long getStat(Stat<Identifier> stat) {
        return playtimeMap.getLong(stat);
    }

    public String format(Stat<Identifier> stat) {
        return LongStatFormatter.fromNormal(((StatAccessor) stat).getFormatter()).format(getStat(stat));
    }

    public abstract int getAFKTimeout();

    public Path getSaveFile() {
        return saveFile;
    }

    public void setSaveFile(Path path) {
        saveFile = path;

        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            Main.LOGGER.error("An error has occured while trying to create the folder ");
            Main.LOGGER.error(e);
        }
    }

    public void load() {
        playtimeMap.putAll(StatParser.parsePlaytimesFile(getSaveFile(), (e) -> {
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

        save();
    }

    public void save() {
        JsonObject playtimes = StatParser.parsePlaytimesJsonClient(playtimeMap);

        try {
            JsonUtil.saveToFile(playtimes, getSaveFile());
        } catch (IOException e) {
            Main.LOGGER.error("Couldn't save playtime stats due to the following error:");
            e.printStackTrace();
        }
    }

    public static int getDefaultAFKTimeout() {
        return 600;
    }

    public static boolean isMovementKeyPressed(GameOptions options) {
        return Stream.of(options.keyForward, options.keyBack, options.keyLeft, options.keyRight, options.keyJump)
                .anyMatch(KeyBinding::isPressed);
    }

    public static boolean isCrafting(PlayerScreenHandler handler) {
        for (int i = 1; i < 5; i++) {
            if (!handler.slots.get(i).getStack().isEmpty()) {
                return true;
            }
        }

        return false;
    }
}
