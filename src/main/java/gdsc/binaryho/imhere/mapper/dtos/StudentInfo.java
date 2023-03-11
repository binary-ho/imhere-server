package gdsc.binaryho.imhere.mapper.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class StudentInfo {

    private Long id;
    private String univId;
    private String name;

    public StudentInfo(Long id, String univId, String name) {
        this.id = id;
        this.univId = univId;
        this.name = name;
    }
}
