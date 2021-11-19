package playtime.meter.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.stat.Stat;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import playtime.meter.ClientStats;
import playtime.meter.PlaytimeMeter;

import java.util.Comparator;

@Environment(EnvType.CLIENT)
public class PlaytimeStatsWidget extends AlwaysSelectedEntryListWidget<PlaytimeStatsWidget.Entry> {
    private final StatsScreen parent;

    public PlaytimeStatsWidget(MinecraftClient minecraftClient, StatsScreen parent) {
        super(minecraftClient, parent.width, parent.height, 32, parent.height - 64, 10);
        this.parent = parent;

        for (Stat<Identifier> stat : ClientStats.PLAYTIME) {
            addEntry(new PlaytimeStatsWidget.Entry(stat));
        }

        children().sort(Comparator.comparing((entry) -> I18n.translate(getStatTranslationKey(entry.stat))));
    }

    protected void renderBackground(MatrixStack matrices) {
        parent.renderBackground(matrices);
    }

    public static String getStatTranslationKey(Stat<Identifier> stat) {
        return "stat." + stat.getValue().toString().replace(':', '.');
    }

    protected class Entry extends AlwaysSelectedEntryListWidget.Entry<playtime.meter.gui.PlaytimeStatsWidget.Entry> {
        private final Stat<Identifier> stat;
        private final Text displayText;

        public Entry(Stat<Identifier> stat) {
            this.stat = stat;
            this.displayText = new TranslatableText(getStatTranslationKey(stat));
        }

        @Override
        public Text getNarration() {
            return new TranslatableText("narrator.select", new LiteralText("").append(displayText)
                    .append(" ").append(getFormatted()));
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX,
                           int mouseY, boolean hovered, float tickDelta) {
            DrawableHelper.drawTextWithShadow(matrices, client.textRenderer, displayText, x + 2, y + 1,
                    index % 2 == 0 ? 16777215 : 9474192);
            String string = this.getFormatted();
            DrawableHelper.drawStringWithShadow(matrices, client.textRenderer, string, x + 2 + 213
                    - client.textRenderer.getWidth(string), y + 1, index % 2 == 0 ? 16777215 : 9474192);
        }

        private String getFormatted() {
            return PlaytimeMeter.getInstance().format(stat);
        }
    }
}
