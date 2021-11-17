package playtime.meter.mixin;

import net.minecraft.stat.Stat;
import net.minecraft.stat.StatFormatter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Stat.class)
public interface StatAccessor {
    @Accessor
    StatFormatter getFormatter();
}
