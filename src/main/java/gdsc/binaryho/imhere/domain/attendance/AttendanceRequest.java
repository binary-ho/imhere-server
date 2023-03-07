package gdsc.binaryho.imhere.domain.attendance;

import lombok.Getter;

@Getter
public class AttendanceRequest {

    private Long lecture_id;
    private Long member_id;
    private String distance;
    private String accuracy;
    private Long milliseconds;
}
