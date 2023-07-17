package gdsc.binaryho.imhere.core.lecture.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LectureCreateRequest {

    @Schema(description = "생성할 강좌 이름")
    private String lectureName;
}
