package gdsc.binaryho.imhere.core.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

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

    @JsonIgnore
    @CreatedDate
    private LocalDateTime createdAt;

    public String getRoleKey() {
        return role.getKey();
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
