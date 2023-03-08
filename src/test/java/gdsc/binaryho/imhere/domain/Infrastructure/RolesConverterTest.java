package gdsc.binaryho.imhere.domain.Infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import gdsc.binaryho.imhere.domain.member.Role;
import gdsc.binaryho.imhere.domain.member.Roles;
import java.util.EnumSet;
import org.junit.jupiter.api.Test;

class RolesConverterTest {

    RolesConverter rolesConverter;

    @Test
    void Roles를_String으로_변환할_수_있다() {
        Roles roles = new Roles(EnumSet.of(Role.ADMIN, Role.LECTURER, Role.STUDENT));

        rolesConverter = new RolesConverter();
        String actualResult = rolesConverter.convertToDatabaseColumn(roles);
        assertThat(actualResult).isEqualTo("ADMIN, LECTURER, STUDENT");
    }

    @Test
    void String을_Roles로_변환할_수_있다() {
        rolesConverter = new RolesConverter();
        Roles expectedResult = new Roles(EnumSet.of(Role.ADMIN, Role.LECTURER, Role.STUDENT));

        Roles actualResult = rolesConverter.convertToEntityAttribute("ADMIN, LECTURER, STUDENT");
        assertThat(actualResult).isEqualTo(expectedResult);
    }
}
