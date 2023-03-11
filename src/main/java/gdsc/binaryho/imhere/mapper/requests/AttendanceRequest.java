package gdsc.binaryho.imhere.mapper.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttendanceRequest {

    private String distance;
    private String accuracy;
    private Long milliseconds;
}
