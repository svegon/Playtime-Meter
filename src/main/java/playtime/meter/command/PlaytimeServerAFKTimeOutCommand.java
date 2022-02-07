package playtime.meter.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import playtime.meter.ServerMain;

public final class PlaytimeServerAFKTimeOutCommand {
    private PlaytimeServerAFKTimeOutCommand() {
        throw new UnsupportedOperationException();
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("playtime:serverafktimeout")
                .then(CommandManager.argument("time", TimeArgumentType.time())
                        .requires(source -> source.hasPermissionLevel(3))
                        .executes(PlaytimeServerAFKTimeOutCommand::execute)));
    }

    private static int execute(final CommandContext<ServerCommandSource> context) {
        int ticks = IntegerArgumentType.getInteger(context, "times");
        int prev = ServerMain.getSettings().setAFKTimeout(ticks);

        if (ticks > 0) {
            context.getSource().sendFeedback(new TranslatableText(
                    "command.playtimer.serverafktimeout.timeoutSet", ticks, prev), false);
        } else {
             context.getSource().sendFeedback(new TranslatableText(
                             "command.playtimer.serverafktimeout.timeoutDisabled"), false);
        }

        return ticks;
    }
}
