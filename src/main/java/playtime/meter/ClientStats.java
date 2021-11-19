package playtime.meter;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ClientStats {
    private ClientStats() {
        throw new AssertionError();
    }

    public static final Registry<Identifier> PLAYTIME_STATS = FabricRegistryBuilder.createDefaulted(Identifier.class,
                    new Identifier("platimer", "playtime_stats"), new Identifier("playtimer",
                            "total_time_played")).buildAndRegister();
    public static final StatType<Identifier> PLAYTIME = registerType("playtime", PLAYTIME_STATS);
    public static final Identifier TOTAL = register("total_time_played");
    public static final Identifier ACTIVE = register("active_playtime");
    public static final Identifier AFK = register("afk_playtime");
    public static final Identifier SCREEN_TIME = register("screen_time");

    private static Identifier register(Identifier id, StatFormatter formatter) {
        Registry.register(PLAYTIME_STATS, id, id);
        PLAYTIME.getOrCreateStat(id, formatter);
        return id;
    }

    private static Identifier register(String id, StatFormatter formatter) {
        return register(new Identifier("playtimer", id), formatter);
    }

    private static Identifier register(String id) {
        return register(id, StatFormatter.TIME);
    }

    private static <T> StatType<T> registerType(String id, Registry<T> registry) {
        return Registry.register(Registry.STAT_TYPE, new Identifier("playtimer", id),
                new StatType<>(registry));
    }
}
