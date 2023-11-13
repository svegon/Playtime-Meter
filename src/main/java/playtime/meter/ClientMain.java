package playtime.meter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import playtime.meter.command.PlaytimeSaveFolderCommand;
import playtime.meter.command.PlaytimeSetAFKTimeOutCommand;
import playtime.meter.util.JsonUtil;
import playtime.meter.util.events.input.KeyBindingPressUpdate;
import playtime.meter.util.stat.ClientPlaytimeMeter;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

public final class ClientMain {
    private ClientMain() {
        throw new UnsupportedOperationException();
    }

    private static Path clientSaveFolder = Main.MOD_DIRECTORY;
    private static ClientPlaytimeMeter playtimeMeter;

    public static void init() {
        playtimeMeter = new ClientPlaytimeMeter(Main.MOD_DIRECTORY.resolve("playtimes.json"));
        Optional<JsonObject> json = Optional.empty();

        try {
            json = JsonUtil.parseFileToObject(Main.MOD_DIRECTORY.resolve("settings.json"));
        } catch (NoSuchFileException ignored) {

        } catch (JsonParseException | IOException e) {
            e.printStackTrace();
        }

        if (json.isPresent()) {
            JsonObject settings = json.get();
            Optional<String> saveFolder = JsonUtil.getProperty(settings, "save_folder")
                    .flatMap(JsonUtil::getAsString);

            saveFolder.ifPresent(s -> setSaveFolder(new File(s).toPath()));
        }
        Directio

        ClientLifecycleEvents.CLIENT_STOPPING.register((client) -> {
            getPlaytimeMeter().save();

            JsonObject settings = new JsonObject();
            JsonObject options = new JsonObject();

            settings.addProperty("save_folder", getPlaytimeMeter().getSaveFile().getParent().toString());
            options.addProperty("afk_timeout", getPlaytimeMeter().getAFKTimeout());

            try {
                JsonUtil.saveToFile(settings, Main.MOD_DIRECTORY.resolve("settings.json"));
                JsonUtil.saveToFile(options, getSaveFolder().resolve("options.json"));
            } catch (IOException e) {
                Main.LOGGER.error("An error has occurred while saving the settings:");
                e.printStackTrace();
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(getPlaytimeMeter());
        KeyBindingPressUpdate.EVENT.register(getPlaytimeMeter());

        getPlaytimeMeter().load();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            PlaytimeSaveFolderCommand.register(dispatcher);
            PlaytimeSetAFKTimeOutCommand.register(dispatcher);
        });
    }

    public static ClientPlaytimeMeter getPlaytimeMeter() {
        return playtimeMeter;
    }

    public static void setSaveFolder(Path saveFolder) {
        getPlaytimeMeter().setSaveFile(saveFolder.resolve("playtimes.json"));
        clientSaveFolder = saveFolder;
    }

    public static Path getSaveFolder() {
        return clientSaveFolder;
    }
}
