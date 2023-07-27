package gdsc.binaryho.imhere.mock;

import gdsc.binaryho.imhere.core.attendance.application.AttendanceService;
import gdsc.binaryho.imhere.core.attendance.application.port.AttendanceNumberRepository;
import gdsc.binaryho.imhere.core.attendance.infrastructure.AttendanceRepository;
import gdsc.binaryho.imhere.core.auth.application.AuthService;
import gdsc.binaryho.imhere.core.auth.application.EmailVerificationService;
import gdsc.binaryho.imhere.core.auth.application.port.MailSender;
import gdsc.binaryho.imhere.core.auth.application.port.VerificationCodeRepository;
import gdsc.binaryho.imhere.core.auth.util.AuthenticationHelper;
import gdsc.binaryho.imhere.core.enrollment.infrastructure.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.core.lecture.application.LectureService;
import gdsc.binaryho.imhere.core.lecture.infrastructure.LectureRepository;
import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestContainer {

    public AttendanceNumberRepository attendanceNumberRepository = new FakeAttendanceNumberRepository();
    public VerificationCodeRepository verificationCodeRepository = new FakeVerificationCodeRepository();
    public boolean isMailSent = false;
    public MailSender mailSender = (recipient, verificationCode) -> isMailSent = true;

    public AuthService authService;
    public EmailVerificationService emailVerificationService;
    public LectureService lectureService;
    public AttendanceService attendanceService;

    public BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Builder
    public TestContainer(
        MemberRepository memberRepository,
        LectureRepository lectureRepository,
        EnrollmentInfoRepository enrollmentInfoRepository,
        AttendanceRepository attendanceRepository) {

        AuthenticationHelper authenticationHelper = new AuthenticationHelper();

        authService = new AuthService(
            memberRepository, verificationCodeRepository, bCryptPasswordEncoder
        );

        emailVerificationService = new EmailVerificationService(mailSender, verificationCodeRepository);

        attendanceService = new AttendanceService(
            authenticationHelper, attendanceRepository, enrollmentInfoRepository, lectureRepository, attendanceNumberRepository
        );

        lectureService = new LectureService(
            authenticationHelper, attendanceService, lectureRepository, enrollmentInfoRepository
        );
    }
}
