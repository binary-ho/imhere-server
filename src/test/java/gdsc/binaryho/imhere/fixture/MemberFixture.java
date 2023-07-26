package gdsc.binaryho.imhere.fixture;

import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MemberFixture {

    public static final String UNIV_ID = "UNIV_ID";
    public static final String NAME = "이진호";
    public static final String RAW_PASSWORD = "abcd1234";
    public static final String PASSWORD =
        new BCryptPasswordEncoder().encode(RAW_PASSWORD);
    public static final Role ROLE = Role.STUDENT;

    public static final Member STUDENT = Member.createMember(UNIV_ID, NAME, PASSWORD, ROLE);
    public static final Member LECTURER = Member.createMember(UNIV_ID, NAME, PASSWORD, Role.LECTURER);
}
