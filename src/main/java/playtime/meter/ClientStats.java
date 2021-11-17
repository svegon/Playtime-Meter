package playtime.meter;

import it.unimi.dsi.fastutil.ints.IntHash;
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

    private static Identifier register(String id) {
        Identifier identifier = new Identifier("playtimer", id);
        Registry.register(PLAYTIME_STATS, identifier, identifier);
        PLAYTIME.getOrCreateStat(identifier, StatFormatter.TIME);
        return identifier;
    }

    private static <T> StatType<T> registerType(String id, Registry<T> registry) {
        return Registry.register(Registry.STAT_TYPE, new Identifier("playtimer", id),
                new StatType<>(registry));
    }
}
