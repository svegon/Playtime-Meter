package playtime.meter.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.text.TranslatableText;
import playtime.meter.PlaytimeMeter;
import playtime.meter.util.LongStatFormatter;

public final class PlaytimeSetAFKTimeOutCommand {
    private PlaytimeSetAFKTimeOutCommand() {
        throw new UnsupportedOperationException();
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("playtime.setafktimeout")
                .then(ClientCommandManager.argument("ticks", TimeArgumentType.time()).executes(context -> {
                    int ticks = PlaytimeMeter.getInstance().setAFKTimeout(IntegerArgumentType.getInteger(context,
                            "ticks"));

                    if (ticks > 0) {
                        context.getSource().sendFeedback(new TranslatableText(
                                "playtimer.playtime.setafktimeout.timeoutSet",
                                LongStatFormatter.TIME.format(ticks)));
                    } else {
                        context.getSource().sendFeedback(new TranslatableText(
                                "playtimer.playtime.setafktimeout.timeoutDisabled"));
                    }

                    return ticks;
                })));
    }
}
