package playtime.meter.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.stat.Stat;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.jetbrains.annotations.NotNull;
import playtime.meter.ClientStats;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;

public final class StatParser {
    private StatParser() {
        throw new UnsupportedOperationException();
    }

    private static final Int2ObjectMap<Object2ObjectMap<Identifier, Identifier>> OLD_MAPPINGS =
            new Int2ObjectOpenHashMap<>();

    public static Object2LongMap<Stat<Identifier>> parsePlaytimesFileClient(final @NotNull Path file,
final @NotNull Consumer<Exception> exceptionHandler) {
        try {
            Optional<JsonObject> playtimes = JsonUtil.parseFileToObject(file);

            if (playtimes.isEmpty()) {
                exceptionHandler.accept(new JsonParseException("The playtimes save file is not a json object."));
            } else {
                Optional<JsonElement> dataVersionJson = JsonUtil.getProperty(playtimes.get(), "DataVersion");
                OptionalInt optionalDataVersion = dataVersionJson.isEmpty() ? OptionalInt.empty()
                        : JsonUtil.getAsInt(dataVersionJson.get());
                int dataVersion = optionalDataVersion.orElse(0);
                JsonObject data = JsonUtil.getProperty(playtimes.get(), "data")
                        .flatMap(JsonUtil::getAsJsonObject).orElseGet(JsonObject::new);

                switch (dataVersion) {
                    case 0:
                        return parsePlaytimesJson(data, exceptionHandler);
                    default:
                        break;
                }
            }
        } catch (NoSuchFileException e) {
            try {
                Optional<JsonObject> playtimes = JsonUtil.parseFileToObject(file.getParent().resolve("save.json"));

                if (playtimes.isEmpty()) {
                    exceptionHandler.accept(new JsonParseException("The playtimes save file is not a json object."));
                } else {
                    return parsePlaytimesJsonLegacy(playtimes.get(), exceptionHandler, OLD_MAPPINGS.get(0));
                }
            } catch (JsonParseException | IOException io) {
                exceptionHandler.accept(e);
            }
        } catch (JsonParseException | IOException e) {
            exceptionHandler.accept(e);
        }

        return Object2LongMaps.emptyMap();
    }

    public static JsonObject parsePlaytimesJson(@NotNull Object2LongMap<Stat<Identifier>> playtimes) {
        JsonObject json = new JsonObject();

        for (Object2LongMap.Entry<Stat<Identifier>> entry : playtimes.object2LongEntrySet()) {
            json.addProperty(entry.getKey().getValue().toString(), entry.getLongValue());
        }

        return json;
    }

    public static JsonObject parsePlaytimesJsonClient(@NotNull Object2LongMap<Stat<Identifier>> playtimes) {
        JsonObject json = new JsonObject();

        json.addProperty("DataVersion", currentDataVersion());
        json.add("data", parsePlaytimesJson(playtimes));

        return json;
    }

    public static Object2LongMap<Stat<Identifier>> parsePlaytimesJson(@NotNull JsonObject playtimes,
                                                                      final @NotNull Consumer<Exception>
                                                                               exceptionHandler) {
        Object2LongMap<Stat<Identifier>> playtimeMap = new Object2LongOpenHashMap<>();

        for (Map.Entry<String, JsonElement> entry : playtimes.entrySet()) {
            Identifier identifier;

            try {
                identifier = new Identifier(entry.getKey());
            } catch (InvalidIdentifierException e) {
                exceptionHandler.accept(e);
                continue;
            }

            if (!ClientStats.PLAYTIME_STATS.containsId(identifier)) {
                exceptionHandler.accept(new InvalidIdentifierException("Invalid client stat identifier: "
                        + identifier));
                continue;
            }

            playtimeMap.put(ClientStats.PLAYTIME.getOrCreateStat(identifier),
                    JsonUtil.getAsLong(entry.getValue()).orElse(0));
        }

        return playtimeMap;
    }

    public static Object2LongMap<Stat<Identifier>> parsePlaytimesJsonLegacy(@NotNull JsonObject playtimes,
                                                                       final @NotNull Consumer<Exception>
                                                                               exceptionHandler,
                                                                            Object2ObjectMap<Identifier, Identifier>
                                                                                    statIdMapping) {
        Object2LongMap<Stat<Identifier>> playtimeMap = new Object2LongOpenHashMap<>();

        for (Map.Entry<String, JsonElement> entry : playtimes.entrySet()) {
            Identifier identifier;

            try {
                identifier = new Identifier(entry.getKey());
            } catch (InvalidIdentifierException e) {
                exceptionHandler.accept(e);
                continue;
            }

            identifier = statIdMapping.getOrDefault(identifier, identifier);

            if (!ClientStats.PLAYTIME_STATS.containsId(identifier)) {
                exceptionHandler.accept(new InvalidIdentifierException("Invalid client stat identifier: "
                        + identifier));
                continue;
            }

            playtimeMap.put(ClientStats.PLAYTIME.getOrCreateStat(identifier),
                    JsonUtil.getAsLong(entry.getValue()).orElse(0));
        }

        return playtimeMap;
    }

    public static int currentDataVersion() {
        return 0;
    }

    static {
        OLD_MAPPINGS.put(0, new Object2ObjectOpenHashMap<>());
    }
}
