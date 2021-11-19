package playtime.meter.util.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.Arrays;

public interface ScreenKeyPressEvent {
    void onScreenKeyPress(int key, int scanCode, int modifiers, boolean action);

    Event<ScreenKeyPressEvent> EVENT = EventFactory.createArrayBacked(ScreenKeyPressEvent.class, listeners ->
            (key, scanCode, modifiers, action) -> Arrays.asList(listeners).forEach(listener ->
                    listener.onScreenKeyPress(key, scanCode, modifiers, action)));
}
