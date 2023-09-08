package gdsc.binaryho.imhere.util;

import java.time.LocalDateTime;

public interface SeoulDateTimeHolder {

    LocalDateTime getSeoulDateTime();

    long getSeoulMilliseconds();

    long getMillisecondsFrom(LocalDateTime localDateTime);

    LocalDateTime from(long milliseconds);
}
