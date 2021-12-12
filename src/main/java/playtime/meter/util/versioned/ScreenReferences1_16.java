package playtime.meter.util.versioned;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class ScreenReferences1_16 implements ScreenReferences {
    private final Method addButtonMethod;
    private final Method addChildMethod;

    ScreenReferences1_16() {
        try {
            addButtonMethod = Screen.class.getDeclaredMethod("method_25411", ClickableWidget.class); // addButton
            addButtonMethod.setAccessible(true);

            addChildMethod = Screen.class.getDeclaredMethod("method_25429", Element.class); // addChild
            addChildMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Element & Drawable> T addDrawableChild(Screen screen, T drawableElement) {
        try {
            return (T) addButtonMethod.invoke(screen, drawableElement);
        } catch (IllegalAccessException | InvocationTargetException e) {
            try {
                return (T) addChildMethod.invoke(screen, drawableElement);
            } catch (InvocationTargetException | IllegalAccessException invocationTargetException) {
                throw new IllegalStateException(e);
            }
        }
    }
}
