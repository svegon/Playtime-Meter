package playtime.meter.util;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

public final class OptionalUtil {
    private OptionalUtil() {
        throw new UnsupportedOperationException();
    }

    public static <T> OptionalInt flatMapToInt(@NotNull Optional<T> optional, Function<? super T, OptionalInt> mapper) {
        Preconditions.checkNotNull(mapper);
        return optional.isEmpty() ? OptionalInt.empty() : mapper.apply(optional.get());
    }
}
