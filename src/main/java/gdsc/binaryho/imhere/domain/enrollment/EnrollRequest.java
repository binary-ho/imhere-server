package gdsc.binaryho.imhere.domain.enrollment;

import java.util.List;
import lombok.Getter;

@Getter
public class EnrollRequest {
    private final List<String> univIds;

    public EnrollRequest(List<String> univIds) {
        this.univIds = univIds;
    }
}
