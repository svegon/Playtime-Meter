package playtime.meter.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.text.TranslatableText;
import playtime.meter.ClientMain;

public final class PlaytimeSetAFKTimeOutCommand {
    private PlaytimeSetAFKTimeOutCommand() {
        throw new UnsupportedOperationException();
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("playtime:setafktimeout")
                .then(ClientCommandManager.argument("ticks", TimeArgumentType.time()).executes(context -> {
                    int ticks = Math.min(IntegerArgumentType.getInteger(context, "ticks"), 6000);
                    int prev = ClientMain.getPlaytimeMeter().setAFKTimeout(ticks);

                    if (ticks > 0) {
                        context.getSource().sendFeedback(new TranslatableText(
                                "command.playtimer.setafktimeout.timeoutSet", ticks, prev));
                    } else {
                        context.getSource().sendFeedback(new TranslatableText(
                                "command.playtimer.setafktimeout.timeoutDisabled"));
                    }

                    return ticks;
                })));
    }
}
