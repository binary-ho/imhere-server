package gdsc.binaryho.imhere.domain.member;

import gdsc.binaryho.imhere.domain.lecturestudent.LectureStudent;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique=true)
    private String univId;
    private String name;
    private String password;

    @Embedded
    private Roles roles;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<LectureStudent> studentLectures = new ArrayList<>();

    public boolean hasRole(Role role) {
        return roles.getRoles().contains(role);
    }

    public static Member createMember(String univId, String name, String password, Roles roles) {
        Member member = new Member();
        member.setUnivId(univId);
        member.setName(name);
        member.setPassword(password);
        member.setRoles(roles);
        return member;
    }
}
