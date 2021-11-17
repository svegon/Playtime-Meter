package playtime.meter.util.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface KeyBindingPressUpdate {
    void onKeyBindingPressUpdate(KeyBinding keyBinding, boolean pressed, CallbackInfo info);

    Event<KeyBindingPressUpdate> EVENT = EventFactory.createArrayBacked(KeyBindingPressUpdate.class,
            (listeners) -> (keyBinding, pressed, info) -> {
        for (KeyBindingPressUpdate listener : listeners) {
            listener.onKeyBindingPressUpdate(keyBinding, pressed, info);

            if (info.isCancelled()) {
                return;
            }
        }
    });
}
