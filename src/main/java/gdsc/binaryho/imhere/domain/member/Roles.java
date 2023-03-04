package gdsc.binaryho.imhere.domain.member;

import java.util.EnumSet;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Roles {

    EnumSet<Role> roles;

    public Roles(EnumSet<Role> roles) {
        this.roles = roles;
    }
}
