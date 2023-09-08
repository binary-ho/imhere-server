package gdsc.binaryho.imhere.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class SeoulDateTime {

    private static final ZoneId SEOUL_ZONE_ID = ZoneId.of("Asia/Seoul");

    public static LocalDateTime getSeoulDateTime() {
        return LocalDateTime.now(SEOUL_ZONE_ID);
    }

    public static long getMillisecondsNow() {
        LocalDateTime seoulDateTime = LocalDateTime.now(SEOUL_ZONE_ID);
        return getMillisecondsFrom(seoulDateTime);
    }

    public static long getMillisecondsFrom(LocalDateTime localDateTime) {
        return localDateTime
            .toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public static LocalDateTime from(long milliseconds) {
        return LocalDateTime
            .ofInstant(Instant.ofEpochMilli(milliseconds), SEOUL_ZONE_ID);
    }
}
