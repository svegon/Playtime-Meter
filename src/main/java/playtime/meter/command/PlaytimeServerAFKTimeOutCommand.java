package playtime.meter.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import playtime.meter.ServerMain;

import java.util.function.Predicate;

public final class PlaytimeServerAFKTimeOutCommand implements Command<ServerCommandSource>,
        Predicate<ServerCommandSource> {
    private static final PlaytimeServerAFKTimeOutCommand INSTANCE = new PlaytimeServerAFKTimeOutCommand();

    private PlaytimeServerAFKTimeOutCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("playtime:serverafktimeout")
                .then(CommandManager.argument("time", TimeArgumentType.time()).requires(INSTANCE)
                        .executes(INSTANCE)));
    }

    public int run(final CommandContext<ServerCommandSource> context) {
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

    @Override
    public boolean test(ServerCommandSource source) {
        return source.hasPermissionLevel(3);
    }
}
