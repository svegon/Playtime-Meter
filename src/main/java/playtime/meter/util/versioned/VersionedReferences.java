package playtime.meter.util.versioned;

import com.google.gson.JsonElement;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.apache.commons.lang3.math.Fraction;
import playtime.meter.util.FabricUtil;

import java.io.Reader;

public final class VersionedReferences {
    private VersionedReferences() {
        throw new UnsupportedOperationException();
    }

    private static final Gson GSON;

    public static void init() {
        // currently, none are necessary
    }

    public static JsonElement parseReader2Json(Reader reader) {
        return GSON.parseReader(reader);
    }

    static {
        if (FabricUtil.isModPresent("minecraft", "1.14.x")) {
            throw new IllegalStateException("unsupported Minecraft version: 1.14.x");
        } else if (FabricUtil.isModPresent("minecraft", "1.15.x")) {
            throw new IllegalStateException("unsupported Minecraft version: 1.15.x");
        } else if (FabricUtil.isModPresent("minecraft", ">1.19")) {
            throw new IllegalStateException("unsupported Minecraft version: " + FabricLoader.getInstance()
                    .getModContainer("minecraft").orElseThrow().getMetadata().getVersion());
        } else if (FabricUtil.isModPresent("minecraft", ">=1.18")) {
            GSON = new Gson1_18();
        } else if (FabricUtil.isModPresent("minecraft", ">=1.16")) {
            GSON = new Gson1_16();
        } else {
            throw new IllegalStateException("unsupported Minecraft version: " + FabricLoader.getInstance()
                    .getModContainer("minecraft").orElseThrow().getMetadata().getVersion());
        }
    }
}
