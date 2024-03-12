package gdsc.binaryho.imhere.mock;

import gdsc.binaryho.imhere.util.SeoulDateTimeHolder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FixedSeoulTimeHolder implements SeoulDateTimeHolder {

    public static final LocalDateTime FIXED_LOCAL_DATE_TIME = LocalDateTime.now();

    public static final Long FIXED_MILLISECONDS = FIXED_LOCAL_DATE_TIME
        .toInstant(ZoneOffset.UTC).toEpochMilli();

    @Override
    public LocalDateTime getSeoulDateTime() {
        return FIXED_LOCAL_DATE_TIME;
    }

    @Override
    public long getSeoulMilliseconds() {
        return FIXED_MILLISECONDS;
    }

    @Override
    public long getMillisecondsFrom(LocalDateTime localDateTime) {
        return FIXED_MILLISECONDS;
    }

    @Override
    public LocalDateTime from(long milliseconds) {
        return FIXED_LOCAL_DATE_TIME;
    }
}
