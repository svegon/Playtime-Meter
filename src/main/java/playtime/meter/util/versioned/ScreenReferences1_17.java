package playtime.meter.util.versioned;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class ScreenReferences1_17 implements ScreenReferences {
    private final Method addDrawableChildMethod;

    ScreenReferences1_17() {
        try {
            addDrawableChildMethod = Screen.class.getDeclaredMethod("method_37063", Element.class);
            addDrawableChildMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Element & Drawable> T addDrawableChild(Screen screen, T drawableElement) {
        try {
            return (T) addDrawableChildMethod.invoke(screen, drawableElement);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}
