package gdsc.binaryho.imhere.mock;

import gdsc.binaryho.imhere.core.attendance.application.AttendanceService;
import gdsc.binaryho.imhere.core.attendance.infrastructure.AttendanceRepository;
import gdsc.binaryho.imhere.core.auth.application.AuthService;
import gdsc.binaryho.imhere.core.auth.application.EmailVerificationService;
import gdsc.binaryho.imhere.core.auth.application.port.MailSender;
import gdsc.binaryho.imhere.core.auth.application.port.VerificationCodeRepository;
import gdsc.binaryho.imhere.core.auth.util.EmailFormValidator;
import gdsc.binaryho.imhere.core.enrollment.application.EnrollmentService;
import gdsc.binaryho.imhere.core.enrollment.infrastructure.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.core.lecture.application.LectureService;
import gdsc.binaryho.imhere.core.lecture.application.OpenLectureService;
import gdsc.binaryho.imhere.core.lecture.application.port.AttendeeCacheRepository;
import gdsc.binaryho.imhere.core.lecture.application.port.OpenLectureCacheRepository;
import gdsc.binaryho.imhere.core.lecture.infrastructure.LectureRepository;
import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import gdsc.binaryho.imhere.mock.fakerepository.FakeAttendeeCacheRepository;
import gdsc.binaryho.imhere.mock.fakerepository.FakeOpenLectureCacheRepository;
import gdsc.binaryho.imhere.mock.fakerepository.FakeVerificationCodeRepository;
import gdsc.binaryho.imhere.security.util.AuthenticationHelper;
import gdsc.binaryho.imhere.util.SeoulDateTimeHolder;
import lombok.Builder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestContainer {

    public final OpenLectureCacheRepository openLectureCacheRepository = new FakeOpenLectureCacheRepository();
    public final AttendeeCacheRepository attendeeCacheRepository = new FakeAttendeeCacheRepository();
    public final VerificationCodeRepository verificationCodeRepository = new FakeVerificationCodeRepository();

    public final AuthService authService;
    public final EmailVerificationService emailVerificationService;
    public final LectureService lectureService;
    public final AttendanceService attendanceService;
    public final OpenLectureService openLectureService;
    public final EnrollmentService enrollmentService;

    public boolean isMailSent = false;
    private final MailSender mailSender = (recipient, verificationCode) -> isMailSent = true;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final AuthenticationHelper authenticationHelper = new AuthenticationHelper();
    private final SeoulDateTimeHolder seoulDateTimeHolder = new FixedSeoulTimeHolder();
    public final EmailFormValidator emailFormValidator = new EmailFormValidator();

    @Builder
    public TestContainer(
        MemberRepository memberRepository,
        LectureRepository lectureRepository,
        EnrollmentInfoRepository enrollmentInfoRepository,
        AttendanceRepository attendanceRepository,
        ApplicationEventPublisher applicationEventPublisher) {

        /* AuthService 초기화 */
        authService = new AuthService(
            memberRepository, bCryptPasswordEncoder
        );

        /* EmailVerificationService 초기화 */
        emailVerificationService = new EmailVerificationService(authService, mailSender, emailFormValidator, verificationCodeRepository);

        /* OpenLectureService 초기화 */
        openLectureService = new OpenLectureService(openLectureCacheRepository);

        enrollmentService = new EnrollmentService(
            authenticationHelper, openLectureService, lectureRepository, enrollmentInfoRepository, applicationEventPublisher
        );

        /* Attendance ervice 초기화 */
        attendanceService = new AttendanceService(
            authenticationHelper, openLectureService, attendanceRepository, enrollmentInfoRepository, lectureRepository, seoulDateTimeHolder
        );

        /* LectureService 초기화 */
        lectureService = new LectureService(
            authenticationHelper, lectureRepository, enrollmentInfoRepository, openLectureCacheRepository, attendeeCacheRepository, applicationEventPublisher, seoulDateTimeHolder
        );
    }
}
