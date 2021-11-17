package playtime.meter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import playtime.meter.command.PlaytimeSetAFKTimeOutCommand;
import playtime.meter.util.events.KeyBindingPressUpdate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Main implements ClientModInitializer, ModInitializer, DedicatedServerModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("playtime-meter");
    public static final Path MOD_DIRECTORY = FabricLoader.getInstance().getGameDir().resolve("Playtime Meter");
    private static PlaytimeMeter playtimeMeter;

    @Override
    public void onInitializeClient() {
        playtimeMeter = new PlaytimeMeter();
        ClientTickEvents.END_CLIENT_TICK.register(playtimeMeter);
        ClientLifecycleEvents.CLIENT_STOPPING.register(playtimeMeter);
        KeyBindingPressUpdate.EVENT.register(playtimeMeter);

        playtimeMeter.load();
        PlaytimeSetAFKTimeOutCommand.register(ClientCommandManager.DISPATCHER);
    }

    @Override
    public void onInitializeServer() {
        throw new UnsupportedOperationException("Sadly server runtime meter hasn't been implemented yet.");
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

    public static PlaytimeMeter getPlaytimeMeter() {
        return playtimeMeter;
    }
}
