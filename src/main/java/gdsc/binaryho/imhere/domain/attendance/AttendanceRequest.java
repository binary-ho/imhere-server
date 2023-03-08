package gdsc.binaryho.imhere.domain.attendance;

import lombok.Getter;

@Getter
public class AttendanceRequest {

    private final String distance;
    private final String accuracy;
    private final Long milliseconds;

    public AttendanceRequest(String distance, String accuracy, Long milliseconds) {
        this.distance = distance;
        this.accuracy = accuracy;
        this.milliseconds = milliseconds;
    }
}
