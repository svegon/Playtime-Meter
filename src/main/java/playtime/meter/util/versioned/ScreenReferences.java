package playtime.meter.util.versioned;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;

public interface ScreenReferences {
    <T extends Element & Drawable> T addDrawableChild(Screen screen, T drawableElement);
}
