package playtime.meter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import playtime.meter.command.PlaytimeSetAFKTimeOutCommand;
import playtime.meter.util.events.KeyBindingPressUpdate;

import java.io.IOException;
import java.nio.file.Files;

public final class Main implements ClientModInitializer, ModInitializer, DedicatedServerModInitializer {

    @Override
    public void onInitializeClient() {
        PlaytimeMeter.playtimeMeter = new PlaytimeMeter();
        ClientTickEvents.END_CLIENT_TICK.register(PlaytimeMeter.playtimeMeter);
        ClientLifecycleEvents.CLIENT_STOPPING.register(PlaytimeMeter.playtimeMeter);
        KeyBindingPressUpdate.EVENT.register(PlaytimeMeter.playtimeMeter);

        PlaytimeMeter.playtimeMeter.load();
        PlaytimeSetAFKTimeOutCommand.register(ClientCommandManager.DISPATCHER);
    }

    @Override
    public void onInitializeServer() {
        throw new UnsupportedOperationException("Sadly server runtime meter hasn't been implemented yet.");
    }

    @Override
    public void onInitialize() {
        try {
            Files.createDirectories(PlaytimeMeter.MOD_DIRECTORY);
        } catch (IOException e) {
            PlaytimeMeter.LOGGER.error("Couldn't create mod directory due to the following error:");
            e.printStackTrace();
        }
    }
}
