package gdsc.binaryho.imhere.domain.member;

import gdsc.binaryho.imhere.domain.roster.LectureStudent;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique=true)
    private Long studentId;
    private String memberName;
    private String password;

    @Embedded
    private Roles roles;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<LectureStudent> studentLectures = new ArrayList<>();
}
