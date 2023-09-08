package gdsc.binaryho.imhere.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

@Component
public class SeoulDateTime implements SeoulDateTimeHolder {

    private static final ZoneId SEOUL_ZONE_ID = ZoneId.of("Asia/Seoul");

    public LocalDateTime getSeoulDateTime() {
        return LocalDateTime.now(SEOUL_ZONE_ID);
    }

    public long getSeoulMilliseconds() {
        LocalDateTime seoulDateTime = LocalDateTime.now(SEOUL_ZONE_ID);
        return getMillisecondsFrom(seoulDateTime);
    }

    public long getMillisecondsFrom(LocalDateTime localDateTime) {
        return localDateTime
            .toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public LocalDateTime from(long milliseconds) {
        return LocalDateTime
            .ofInstant(Instant.ofEpochMilli(milliseconds), SEOUL_ZONE_ID);
    }
}
