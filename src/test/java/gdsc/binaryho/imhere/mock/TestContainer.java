package gdsc.binaryho.imhere.mock;

import gdsc.binaryho.imhere.core.attendance.application.AttendanceService;
import gdsc.binaryho.imhere.core.attendance.infrastructure.AttendanceRepository;
import gdsc.binaryho.imhere.core.auth.application.AuthService;
import gdsc.binaryho.imhere.core.auth.application.EmailVerificationService;
import gdsc.binaryho.imhere.core.auth.application.port.MailSender;
import gdsc.binaryho.imhere.core.auth.application.port.VerificationCodeRepository;
import gdsc.binaryho.imhere.core.enrollment.infrastructure.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.core.lecture.application.port.OpenLectureRepository;
import gdsc.binaryho.imhere.core.lecture.infrastructure.LectureRepository;
import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import gdsc.binaryho.imhere.security.util.AuthenticationHelper;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestContainer {

    public final OpenLectureRepository openLectureRepository = new FakeOpenLectureRepository();
    public final VerificationCodeRepository verificationCodeRepository = new FakeVerificationCodeRepository();

    public final AuthService authService;
    public final EmailVerificationService emailVerificationService;
//    public final LectureService lectureService;
    public final AttendanceService attendanceService;

    public boolean isMailSent = false;
    private final MailSender mailSender = (recipient, verificationCode) -> isMailSent = true;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final AuthenticationHelper authenticationHelper = new AuthenticationHelper();

    @Builder
    public TestContainer(
        MemberRepository memberRepository,
        LectureRepository lectureRepository,
        EnrollmentInfoRepository enrollmentInfoRepository,
        AttendanceRepository attendanceRepository) {

        authService = new AuthService(
            memberRepository, verificationCodeRepository, bCryptPasswordEncoder
        );

        emailVerificationService = new EmailVerificationService(mailSender, verificationCodeRepository);

        attendanceService = new AttendanceService(
            authenticationHelper, attendanceRepository, enrollmentInfoRepository, lectureRepository, openLectureRepository
        );

        lectureService = new LectureService(
            authenticationHelper, attendanceService, lectureRepository, enrollmentInfoRepository
        );
    }
}
