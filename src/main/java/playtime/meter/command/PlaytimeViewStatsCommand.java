package playtime.meter.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import playtime.meter.PlaytimeStats;
import playtime.meter.gui.PlaytimeStatsWidget;
import playtime.meter.mixinterfaces.IServerPlayerEntity;
import playtime.meter.util.stat.PlaytimeMeter;

public final class PlaytimeViewStatsCommand {
    private PlaytimeViewStatsCommand() {
        throw new UnsupportedOperationException();
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("playtime:viewstats")
                .requires(source -> source.getEntity() instanceof ServerPlayerEntity)
                .executes(PlaytimeViewStatsCommand::execute));
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        PlaytimeMeter meter = ((IServerPlayerEntity) player).getPlaytimeMeter();

        for (Stat<Identifier> stat : PlaytimeStats.PLAYTIME) {
            context.getSource().sendFeedback(new TranslatableText(PlaytimeStatsWidget.getStatTranslationKey(stat))
                    .append(":\t\t\t\t").append(meter.format(stat)), false);
        }

        return PlaytimeStats.PLAYTIME_STATS.size();
    }
}
