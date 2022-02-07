package playtime.meter;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class PlaytimeStats {
    private PlaytimeStats() {
        throw new AssertionError();
    }

    public static final Registry<Identifier> PLAYTIME_STATS = FabricRegistryBuilder.createSimple(Identifier.class,
                    Main.modIdentifier("playtime_stats")).buildAndRegister();
    public static final StatType<Identifier> PLAYTIME = registerType("playtime", PLAYTIME_STATS);

    public static final Stat<Identifier> TOTAL = register("total_time_played");
    public static final Stat<Identifier> AFK = register("afk_playtime");

    public static final Stat<Identifier> ACTIVE = register("active_playtime");
    public static final Stat<Identifier> WALKING_TIME = register("walk_time");
    public static final Stat<Identifier> STANDING_TIME = register("stand_time");
    public static final Stat<Identifier> FALLING_TIME = register("fall_time");
    public static final Stat<Identifier> MINING_TIME = register("mine_time");
    public static final Stat<Identifier> BUILDING_TIME = register("build_time");
    public static final Stat<Identifier> CRAFTING_TIME = register("craft_time");
    public static final Stat<Identifier> FIGHTING_TIME = register("fight_time");

    public static final Stat<Identifier> SCREEN_TIME = register("screen_time");
    public static final Stat<Identifier> TITLE_SCREEN_TIME = register("title_screen_time");
    public static final Stat<Identifier> WORLD_SCREEN_TIME = register("world_screen_time");
    public static final Stat<Identifier> LOADING_SCREEN_TIME = register("loading_screen_time");
    public static final Stat<Identifier> PAUSE_SCREEN_TIME = register("pause_screen_time");

    private static Stat<Identifier> register(String id, StatFormatter formatter) {
        Identifier identifier = Main.modIdentifier(id);
        Registry.register(PLAYTIME_STATS, identifier, identifier);
        return PLAYTIME.getOrCreateStat(identifier, formatter);
    }

    private static Stat<Identifier> register(String id) {
        return register(id, StatFormatter.TIME);
    }

    private static <T> StatType<T> registerType(String id, Registry<T> registry) {
        return Registry.register(Registry.STAT_TYPE, Main.modIdentifier(id),
                new StatType<>(registry));
    }
}
