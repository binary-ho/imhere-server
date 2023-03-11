package gdsc.binaryho.imhere.mapper.requests;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EnrollRequest {
    private List<String> univIds;
}
