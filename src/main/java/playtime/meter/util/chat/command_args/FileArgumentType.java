package playtime.meter.util.chat.command_args;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class FileArgumentType implements ArgumentType<Path> {
    private static final Collection<String> EXAMPLES = ImmutableList.of("C:/Playtime Meter", "Playtimes",
            "../Playtime Meter");

    @NotNull
    private final Path directory;

    private FileArgumentType(@NotNull Path directory) {
        this.directory = directory;
    }

    public static FileArgumentType runDirectory() {
        return new FileArgumentType(FabricLoader.getInstance().getGameDir());
    }

    @Override
    public Path parse(StringReader reader) throws CommandSyntaxException {
        final String s = reader.readString().replaceAll("\\\\", "/");

        if (s.length() < 2) {
            return directory;
        }

        return s.charAt(1) == ':' ? new File(s).toPath() : directory.resolve(s);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Arrays.stream(Optional.ofNullable(directory.toFile().listFiles())
                        .orElseGet(() -> new File[0])).filter(File::isDirectory).map(f -> "\"" + f.getName() + "\""),
                builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof FileArgumentType that))
            return false;

        return directory.equals(that.directory);
    }

    @Override
    public int hashCode() {
        return directory.hashCode();
    }

    @Override
    public String toString() {
        return "FileArgumentType(" + directory + ")";
    }

    public static Path getPath(CommandContext<FabricClientCommandSource> context, String name) {
        return context.getArgument(name, Path.class);
    }
}
