package playtime.meter.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import playtime.meter.util.events.KeyBindingPressUpdate;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
    @Inject(method = {"setKeyPressed"}, at = {@At(value = "INVOKE",
            target = "Lnet/minecraft/client/option/KeyBinding;setPressed(Z)V")}, cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD)
    private static void onUpdateKeyBindingState(InputUtil.Key key, boolean pressed, CallbackInfo info,
                                                KeyBinding keyBinding) {
        KeyBindingPressUpdate.EVENT.invoker().onKeyBindingPressUpdate(keyBinding, pressed, info);
    }
}
