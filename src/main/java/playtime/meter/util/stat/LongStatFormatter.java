package playtime.meter.util.stat;

import net.minecraft.stat.StatFormatter;

import java.text.NumberFormat;

@FunctionalInterface
public interface LongStatFormatter {
    LongStatFormatter DEFAULT = new LongStatFormatter() {
        final NumberFormat intFormat = NumberFormat.getIntegerInstance();

        @Override
        public String format(long longStat) {
            return intFormat.format(longStat);
        }
    };
    LongStatFormatter DIVIDE_BY_TEN = (v) -> StatFormatter.DECIMAL_FORMAT.format(v * 0.1D);
    LongStatFormatter DISTANCE = (v) -> {
        double d = (double) v / 100.0D;
        double e = d / 1000.0D;

        if (e > 0.5D) {
            return StatFormatter.DECIMAL_FORMAT.format(e) + " km";
        } else {
            return d > 0.5D ? StatFormatter.DECIMAL_FORMAT.format(d) + " m" : v + " cm";
        }
    };
    LongStatFormatter TIME = (i) -> {
        StringBuilder builder = new StringBuilder();

        long d = i / 1728000;
        i -= d * 1728000;
        long h = i / 72000;
        i -= h * 72000;
        long m = i / 1200;
        i -= m * 1200;
        long s = i / 20;
        i -= s * 20;

        if (d != 0) {
            builder.append(d).append(" y");
        }

        if (h != 0) {
            builder.append(" ").append(h).append(" h");
        }

        if (m != 0) {
            builder.append(" ").append(m).append(" m");
        }

        if (s != 0 || i != 0) {
            builder.append(" ").append(s);

            if (i != 0) {
                builder.append(".").append(50 * i);
            }

            builder.append(" s");
        }

        return builder.toString();
    };

    String format(long longStat);

    static LongStatFormatter fromNormal(final StatFormatter intFormatter) {
        if (intFormatter == StatFormatter.TIME) {
            return LongStatFormatter.TIME;
        } else if (intFormatter == StatFormatter.DEFAULT) {
            return LongStatFormatter.DEFAULT;
        } else if (intFormatter == StatFormatter.DISTANCE) {
            return LongStatFormatter.DISTANCE;
        } else if (intFormatter == StatFormatter.DIVIDE_BY_TEN) {
            return LongStatFormatter.DIVIDE_BY_TEN;
        } else {
            return (i) -> intFormatter.format((int) Math.min(i, 2147483647L));
        }
    }
}
