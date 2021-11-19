package playtime.meter.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import playtime.meter.util.events.ScreenKeyPressEvent;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Inject(method = {"onKey"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;" +
            "wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V")}, cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void onKeyMixin(long window, int key, int scancode, int action, int modifiers, CallbackInfo info,
                            Screen screen, boolean[] bls) {
        ScreenKeyPressEvent.EVENT.invoker().onScreenKeyPress(key, scancode, modifiers, bls[0]);
    }
}
