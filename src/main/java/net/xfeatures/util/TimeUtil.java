package net.xfeatures.util;

import net.xfeatures.config.MessagesConfig;

import java.time.Duration;

public class TimeUtil {
    private TimeUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String formatDuration(long millis, MessagesConfig messages) {
        if (millis < 0) millis = 0;

        Duration duration = Duration.ofMillis(millis);
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(messages.formatNoColor("time-days", "days", days));
        }
        if (hours > 0 || days > 0) {
            sb.append(messages.formatNoColor("time-hours", "hours", hours));
        }
        if (minutes > 0 || hours > 0 || days > 0) {
            sb.append(messages.formatNoColor("time-minutes", "minutes", minutes));
        }
        sb.append(messages.formatNoColor("time-seconds", "seconds", seconds));

        return sb.toString().trim();
    }
}
