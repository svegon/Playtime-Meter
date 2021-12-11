package playtime.meter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import playtime.meter.command.PlaytimeSaveFolderCommand;
import playtime.meter.command.PlaytimeSetAFKTimeOutCommand;
import playtime.meter.util.stat.ClientPlaytimeMeter;
import playtime.meter.util.JsonUtil;
import playtime.meter.util.events.KeyBindingPressUpdate;
import playtime.meter.util.stat.PlaytimeMeter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

public final class Main implements ClientModInitializer, ModInitializer, DedicatedServerModInitializer,
        ClientLifecycleEvents.ClientStopping {
    public static final Logger LOGGER = LogManager.getLogger("playtime-meter");
    public static final ModContainer MOD = FabricLoader.getInstance().getModContainer("playtimer").get();
    public static final Path MOD_DIRECTORY = FabricLoader.getInstance().getGameDir().resolve("Playtime Meter");
    private static PlaytimeMeter playtimeMeter;

    @Override
    public void onInitializeClient() {
        playtimeMeter = new ClientPlaytimeMeter(MOD_DIRECTORY);
        Optional<JsonObject> json = Optional.empty();

        try {
            json = JsonUtil.parseFileToObject(MOD_DIRECTORY.resolve("settings.json"));
        } catch (NoSuchFileException ignored) {

        } catch (JsonParseException | IOException e) {
            e.printStackTrace();
        }

        if (json.isPresent()) {
            JsonObject settings = json.get();
            Optional<String> saveFolder = JsonUtil.getProperty(settings, "save_folder")
                    .flatMap(JsonUtil::getAsString);

            saveFolder.ifPresent(s -> playtimeMeter.setSaveFolder(new File(s).toPath()));
        }

        ClientTickEvents.END_CLIENT_TICK.register((ClientTickEvents.EndTick) playtimeMeter);
        ClientLifecycleEvents.CLIENT_STOPPING.register(this);
        KeyBindingPressUpdate.EVENT.register((KeyBindingPressUpdate) playtimeMeter);
        playtimeMeter.load();

        PlaytimeSetAFKTimeOutCommand.register(ClientCommandManager.DISPATCHER);
        PlaytimeSaveFolderCommand.register(ClientCommandManager.DISPATCHER);
    }

    @Override
    public void onInitializeServer() {
        throw new UnsupportedOperationException("Sadly server playtime meter hasn't been implemented yet.");
    }

    @Override
    public void onInitialize() {
        try {
            Files.createDirectories(MOD_DIRECTORY);
        } catch (IOException e) {
            LOGGER.error("Couldn't create mod directory due to the following error:");
            e.printStackTrace();
        }
    }

    public static Identifier modIdentifier(String path) {
        return new Identifier("playtimer", path);
    }

    public static PlaytimeMeter getPlaytimeMeter() {
        return playtimeMeter;
    }

    @Override
    public void onClientStopping(MinecraftClient client) {
        getPlaytimeMeter().save();

        JsonObject settings = new JsonObject();

        settings.addProperty("save_folder", getPlaytimeMeter().getSaveFolder().toString());

        try {
            JsonUtil.saveToFile(settings, MOD_DIRECTORY.resolve("settings.json"));
        } catch (IOException e) {
            Main.LOGGER.error("An error has occurred while saving the settings:");
            e.printStackTrace();
        }
    }
}
