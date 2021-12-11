package playtime.meter;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ClientStats {
    private ClientStats() {
        throw new AssertionError();
    }

    public static final Registry<Identifier> PLAYTIME_STATS = FabricRegistryBuilder.createSimple(Identifier.class,
                    Main.modIdentifier("playtime_stats")).buildAndRegister();
    public static final StatType<Identifier> PLAYTIME = registerType("playtime", PLAYTIME_STATS);
    public static final Stat<Identifier> TOTAL = register("total_time_played");
    public static final Stat<Identifier> ACTIVE = register("active_playtime");
    public static final Stat<Identifier> AFK = register("afk_playtime");
    public static final Stat<Identifier> SCREEN_TIME = register("screen_time");

    private static Stat<Identifier> register(String id) {
        Identifier identifier = Main.modIdentifier(id);
        Registry.register(PLAYTIME_STATS, identifier, identifier);
        return PLAYTIME.getOrCreateStat(identifier, StatFormatter.TIME);
    }

    private static <T> StatType<T> registerType(String id, Registry<T> registry) {
        return Registry.register(Registry.STAT_TYPE, Main.modIdentifier(id),
                new StatType<>(registry));
    }
}
