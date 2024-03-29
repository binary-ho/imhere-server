package gdsc.binaryho.imhere.core.enrollment.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EnrollmentRequestForLecturer {

    @Schema(description = "학생 id 리스트")
    private List<String> univIds;
}
