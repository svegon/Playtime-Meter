package playtime.meter;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.util.version.SemanticVersionImpl;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import playtime.meter.command.PlaytimeServerAFKTimeOutCommand;
import playtime.meter.command.PlaytimeViewStatsCommand;
import playtime.meter.util.versioned.VersionedReferences;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Main implements ClientModInitializer, CommandRegistrationCallback, ModInitializer,
        DedicatedServerModInitializer, PreLaunchEntrypoint {
    public static final Logger LOGGER = LogManager.getLogger("playtime-meter");
    public static final Path MOD_DIRECTORY = FabricLoader.getInstance().getGameDir().resolve("Playtime Meter");

    @Override
    public void onInitialize() {
        try {
            Files.createDirectories(MOD_DIRECTORY);
        } catch (IOException e) {
            LOGGER.error("Couldn't create mod directory due to the following error:");
            e.printStackTrace();
        }

        CommandRegistrationCallback.EVENT.register(this);
    }

    @Override
    public void onInitializeClient() {
        ClientMain.init();
    }

    @Override
    public void onInitializeServer() {
        ServerMain.init();
    }

    @Override
    public void onPreLaunch() {
        VersionedReferences.init();
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        PlaytimeServerAFKTimeOutCommand.register(dispatcher, dedicated);
        PlaytimeViewStatsCommand.register(dispatcher, dedicated);
    }

    public static Identifier modIdentifier(String path) {
        return new Identifier("playtimer", path);
    }
}
