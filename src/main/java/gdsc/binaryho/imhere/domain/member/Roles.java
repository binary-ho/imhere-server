package gdsc.binaryho.imhere.domain.member;

import java.util.EnumSet;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Roles {

    EnumSet<Role> roles;

    public Roles(EnumSet<Role> roles) {
        this.roles = roles;
    }
}
