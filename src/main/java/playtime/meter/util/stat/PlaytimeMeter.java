package playtime.meter.util.stat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import playtime.meter.mixin.StatAccessor;

import java.nio.file.Path;

/**
 * a copy of {@code StatHandler} which saves values as long instead of int to support numbers larger
 * than {@code Integer.MAX_VALUE}
 */
public abstract class PlaytimeMeter {
    private Path saveFolder;

    public PlaytimeMeter(Path saveFolder) {
        this.saveFolder = saveFolder;
    }

    public void increaseStat(PlayerEntity player, Stat<Identifier> stat, long value) {
        this.setStat(player, stat, this.getStat(player, stat) + value);
    }

    public abstract void setStat(PlayerEntity player, Stat<Identifier> stat, long value);

    public long getStat(PlayerEntity player, @NotNull StatType<Identifier> type, Identifier stat) {
        return type.hasStat(stat) ? this.getStat(player, type.getOrCreateStat(stat)) : 0;
    }

    public abstract long getStat(PlayerEntity player, Stat<Identifier> stat);

    public final String format(PlayerEntity player, Stat<Identifier> stat) {
        return LongStatFormatter.fromNormal(((StatAccessor) stat).getFormatter()).format(getStat(player, stat));
    }

    public abstract int setAFKTimeout(PlayerEntity player, int timeout);

    public abstract int getAFKTimeout(PlayerEntity player);

    public abstract void load();

    public abstract void save();

    public Path getSaveFolder() {
        return saveFolder;
    }

    public void setSaveFolder(Path path) {
        saveFolder = path;
    }
}
