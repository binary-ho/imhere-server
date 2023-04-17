package gdsc.binaryho.imhere;

import gdsc.binaryho.imhere.domain.member.Role;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockSecurityContextFactory.class)
public @interface MockMember {

    long id() default 1L;
    String univId() default "mockMember";
    String name() default "member";
    String password() default "password";
    Role role() default Role.STUDENT;
}