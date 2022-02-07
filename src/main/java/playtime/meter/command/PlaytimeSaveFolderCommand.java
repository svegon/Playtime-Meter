package playtime.meter.command;

import com.google.gson.JsonParseException;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.stat.Stat;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import playtime.meter.ClientMain;
import playtime.meter.util.StatParser;
import playtime.meter.util.chat.command_args.FileArgumentType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public final class PlaytimeSaveFolderCommand {
    private PlaytimeSaveFolderCommand() {
        throw new UnsupportedOperationException();
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("playtime:savefolder")
                .then(ClientCommandManager.literal("get").executes(context -> {
                    context.getSource().sendFeedback(new TranslatableText("playtimer.command.playtime.savefolder" +
                            ".get.successful", ClientMain.getPlaytimeMeter().getSaveFile()));
                    return 1;
                })).then(ClientCommandManager.literal("overwrite").then(ClientCommandManager.argument(
                        "folder", FileArgumentType.runDirectory())
                        .executes(PlaytimeSaveFolderCommand::setFolder)))
                .then(ClientCommandManager.literal("set").then(ClientCommandManager.argument("folder",
                        FileArgumentType.runDirectory()).executes(context -> {
                    int ret = setFolder(context);
                    ClientMain.getPlaytimeMeter().load();
                    return ret;
                }))).then(ClientCommandManager.literal("append").then(ClientCommandManager.argument("folder",
                        FileArgumentType.runDirectory()).executes(context -> {
                    final AtomicInteger i = new AtomicInteger(setFolder(context));

                    if (i.get() != 1) {
                        return i.get();
                    }

                    Object2LongMap<Stat<Identifier>> playtimes = StatParser.parsePlaytimesFile(
                            ClientMain.getPlaytimeMeter().getSaveFile(), (e) -> {
                                if (e instanceof InvalidIdentifierException) {
                                    context.getSource().sendError(new LiteralText(e.getMessage()));
                                } else if (e instanceof NoSuchFileException) {
                                    context.getSource().sendError(new TranslatableText(
                                            "command.playtimer.savefolder.append.noSuchFile",
                                            ClientMain.getPlaytimeMeter().getSaveFile().resolve("playtimes.json")));
                                    i.set(0);
                                } else if (e instanceof JsonParseException || e instanceof IOException) {
                                    context.getSource().sendError(new TranslatableText(
                                            "command.playtimer.savefolder.append.exception"));
                                    e.printStackTrace();
                                    i.set(-2);
                                }
                            });

                    if (i.get() == 1) {
                        for (Object2LongMap.Entry<Stat<Identifier>> entry : playtimes.object2LongEntrySet()) {
                            ClientMain.getPlaytimeMeter().increaseStat(entry.getKey(), entry.getLongValue());
                        }
                    }

                    return i.get();
                }))));
    }

    private static int setFolder(CommandContext<FabricClientCommandSource> context) {
        Path oldFolder = ClientMain.getSaveFolder();
        Path newFolder = FileArgumentType.getPath(context, "folder");

        if (oldFolder.equals(newFolder)) {
            context.getSource().sendError(new TranslatableText(
                    "command.playtimer.savefolder.set.sameFolder", newFolder));
            return 0;
        }

        try {
            Files.createDirectories(newFolder);
        } catch (IOException e) {
            context.getSource().sendError(new TranslatableText("command.playtimer.savefolder.set.iOException"));
            e.printStackTrace();
            return -1;
        }

        ClientMain.setSaveFolder(newFolder);
        context.getSource().sendFeedback(new TranslatableText(
                "command.playtimer.savefolder.set.successful", oldFolder, newFolder));
        return 1;
    }
}
