package playtime.meter.mixin;

import net.minecraft.client.Mouse;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import playtime.meter.util.events.ScreenKeyPressEvent;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Inject(method = {"onMouseButton"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;" +
            "wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V")}, cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void onMouseButtonMixin(long window, int button, int action, int mods, CallbackInfo info, boolean bl,
                                    int $synthetic, boolean[] bls) {
        ScreenKeyPressEvent.EVENT.invoker().onScreenKeyPress(button,
                InputUtil.Type.MOUSE.createFromCode(button).getCode(), mods, bls[0]);
    }
}
