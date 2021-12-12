package playtime.meter.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.impl.util.version.SemanticVersionImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class FabricUtil {
    private FabricUtil() {
        throw new UnsupportedOperationException();
    }

    public static boolean isModPresent(@NotNull String name) {
        return isModPresent(name, "*");
    }

    public static boolean isModPresent(@NotNull String name, final @NotNull String @NotNull ... versions) {
        return isModPresent(name, (mod) -> {
            final Version modVersion = mod.getMetadata().getVersion();

            return Arrays.stream(versions).anyMatch((v) -> versionPredicate(v).test(modVersion));
        });
    }

    public static boolean isModPresent(@NotNull String name, @NotNull Predicate<ModContainer> modPredicate) {
        return FabricLoader.getInstance().getModContainer(name).stream().anyMatch(modPredicate);
    }

    public static Predicate<Version> versionPredicate(@NotNull String version) {
        if (version.startsWith(">") || version.startsWith("<") || version.startsWith("=") || version.startsWith("!")) {
            boolean greater = false;
            boolean less = false;
            boolean equal = false;

            if (version.charAt(0) == '=') {
                equal = true;
                version = version.substring(1);
            } else if (version.charAt(0) == '!') {
                less = true;
                greater = true;
                version = version.substring(version.charAt(1) == '=' ? 2 : 1);
            } else if (version.charAt(0) == '<') {
                less = true;

                if (version.charAt(1) == '=') {
                    equal = true;
                    version = version.substring(2);
                } else {
                    version = version.substring(1);
                }
            } else if (version.charAt(0) == '>') {
                greater = true;

                if (version.charAt(1) == '=') {
                    equal = true;
                    version = version.substring(2);
                } else {
                    version = version.substring(1);
                }
            }

            boolean finalLess = less;
            boolean finalGreater = greater;
            boolean finalEqual = equal;
            final Version version1;

            try {
                version1 = new SemanticVersionImpl(version, true);
            } catch (VersionParsingException e) {
                throw new IllegalArgumentException(e);
            }

            return v -> {
                int c = v.compareTo(version1);

                return finalGreater && c > 0 || finalEqual && c == 0 || finalLess && c < 0;
            };
        }

        final Pattern pattern = Pattern.compile(version.replace("x", "[0-9]"));
        return v -> pattern.matcher(v.getFriendlyString()).matches();
    }
}
