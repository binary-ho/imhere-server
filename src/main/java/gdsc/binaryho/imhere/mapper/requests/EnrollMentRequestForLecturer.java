package gdsc.binaryho.imhere.mapper.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EnrollMentRequestForLecturer {

    @Schema(description = "학생 id 리스트")
    private List<String> univIds;
}
