package playtime.meter.command;

import com.mojang.brigadier.Command;
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

import java.util.function.Predicate;

public final class PlaytimeViewStatsCommand implements Command<ServerCommandSource>, Predicate<ServerCommandSource> {
    private static final PlaytimeViewStatsCommand INSTANCE = new PlaytimeViewStatsCommand();

    private PlaytimeViewStatsCommand() {

    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("playtime:viewstats").requires(INSTANCE).executes(INSTANCE));
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        PlaytimeMeter meter = ((IServerPlayerEntity) player).getPlaytimeMeter();
        int i = 0;

        for (Stat<Identifier> stat : PlaytimeStats.PLAYTIME) {
            context.getSource().sendFeedback(new TranslatableText(PlaytimeStatsWidget.getStatTranslationKey(stat))
                    .append(": ").append(meter.format(stat)), false);
            i++;
        }

        return i;
    }

    @Override
    public boolean test(ServerCommandSource source) {
        return source.getEntity() instanceof ServerPlayerEntity;
    }
}
