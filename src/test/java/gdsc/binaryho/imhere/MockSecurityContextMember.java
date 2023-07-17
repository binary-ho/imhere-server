package gdsc.binaryho.imhere;

import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.fixture.MemberFixture;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockSecurityContextFactory.class)
public @interface MockSecurityContextMember {

    long id() default 1L;
    String univId() default MemberFixture.UNIV_ID;
    String name() default MemberFixture.NAME;
    String password() default MemberFixture.RAW_PASSWORD;
    Role role() default Role.STUDENT;
}
