package playtime.meter.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsListener;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import playtime.meter.gui.PlaytimeStatsWidget;

@Mixin(StatsScreen.class)
public abstract class StatsScreenMixin extends Screen implements StatsListener {
    private StatsScreenMixin(Text title) {
        super(title);
    }

    @Unique
    private PlaytimeStatsWidget playtime;
    
    @Inject(method = {"createLists"}, at = {@At("TAIL")})
    private void onCreateLists(CallbackInfo info) {
        playtime = new PlaytimeStatsWidget(client, (StatsScreen) (Object) this);
    }

    @Inject(method = {"createButtons"}, at = {@At("TAIL")})
    private void onCreateButtons(CallbackInfo info) {
        addDrawableChild(new ButtonWidget(width / 2 + 80, height - 52, 80,
                20, Text.translatable("stat.playtimeButton"), (button) -> selectStatList(playtime)));
    }

    @ModifyArg(method = {"createButtons"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIIILnet/minecraft/text/Text;" +
                    "Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)V", ordinal = 0), index = 0)
    private int adjustGeneralStatsButtonX(int x) {
        return x - 40;
    }

    @ModifyArg(method = {"createButtons"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIIILnet/minecraft/text/Text;" +
                    "Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)V", ordinal = 1), index = 0)
    private int adjustItemStatsButtonX(int x) {
        return x - 40;
    }

    @ModifyArg(method = {"createButtons"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIIILnet/minecraft/text/Text;" +
                    "Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)V", ordinal = 2), index = 0)
    private int adjustMobStatsButtonX(int x) {
        return x - 40;
    }

    @Shadow
    public abstract void selectStatList(@Nullable AlwaysSelectedEntryListWidget<?> list);
}
