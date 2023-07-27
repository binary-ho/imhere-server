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

    public static final Long STUDENT_ID = 1L;
    public static final Long LECTURER_ID = 2L;
    public static final Member STUDENT = createMockStudent();
    public static final Member LECTURER = createMockLecturer();

    private static Member createMockStudent() {
        Member student = Member.createMember(UNIV_ID, NAME, PASSWORD, Role.STUDENT);
        student.setId(STUDENT_ID);
        return student;
    }

    private static Member createMockLecturer() {
        Member lecturer = Member.createMember(UNIV_ID, NAME, PASSWORD, Role.STUDENT);
        lecturer.setId(LECTURER_ID);
        return lecturer;
    }
}
