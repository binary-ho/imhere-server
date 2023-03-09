package gdsc.binaryho.imhere.domain.member;

import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "members")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String univId;
    @Column(nullable = false)
    private String name;
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<EnrollmentInfo> enrollmentInfos = new ArrayList<>();

    public boolean hasRole(Role role) {
        return role.equals(role);
    }

    public static Member createMember(String univId, String name, String password, Role role) {
        Member member = new Member();
        member.setUnivId(univId);
        member.setName(name);
        member.setPassword(password);
        member.setRole(role);
        return member;
    }
}
