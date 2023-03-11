package gdsc.binaryho.imhere.mapper.requests;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EnrollMentRequestForLecturer {
    private List<String> univIds;
}
