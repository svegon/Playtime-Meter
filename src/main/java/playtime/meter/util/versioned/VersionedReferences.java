package playtime.meter.util.versioned;

import com.google.gson.JsonElement;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import playtime.meter.util.FabricUtil;

import java.io.Reader;

public final class VersionedReferences {
    private VersionedReferences() {
        throw new UnsupportedOperationException();
    }

    private static final Gson GSON;
    private static final ScreenReferences SCREEN;   

    public static void initVersionedMixins() {
        // currently, none are necessary
    }

    public static JsonElement parseReader2Json(Reader reader) {
        return GSON.parseReader(reader);
    }

    public static <T extends Element & Drawable> T addDrawableChild(Screen screen, T drawableElement) {
        return SCREEN.addDrawableChild(screen, drawableElement);
    }

    static {
        if (FabricUtil.isModPresent("minecraft", "1.14.x")) {
            throw new IllegalStateException("unsupported Minecraft version: 1.14.x");
        } else if (FabricUtil.isModPresent("minecraft", "1.15.x")) {
            throw new IllegalStateException("unsupported Minecraft version: 1.15.x");
        } else if (FabricUtil.isModPresent("minecraft", "1.16.x")) {
            GSON = new Gson1_16();
            SCREEN = new ScreenReferences1_16();
        } else if (FabricUtil.isModPresent("minecraft", "1.17.x")) {
            GSON = new Gson1_16();
            SCREEN = new ScreenReferences1_17();
        } else if (FabricUtil.isModPresent("minecraft", "1.18.x")) {
            GSON = new Gson1_18();
            SCREEN = new ScreenReferences1_17();
        } else {
            throw new IllegalStateException("unsupported Minecraft version: " + FabricLoader.getInstance()
                    .getModContainer("minecraft").orElseThrow().getMetadata().getVersion());
        }
    }
}
