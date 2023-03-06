package gdsc.binaryho.imhere.domain.enrollment;

import java.util.List;
import lombok.Getter;

@Getter
public class EnrollRequest {
    private Long lectureId;
    private List<String> univIds;
}
