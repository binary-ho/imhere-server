package gdsc.binaryho.imhere.mock;

import gdsc.binaryho.imhere.core.attendance.application.LecturerAttendanceService;
import gdsc.binaryho.imhere.core.attendance.application.StudentAttendanceService;
import gdsc.binaryho.imhere.core.attendance.application.port.AttendanceHistoryCacheRepository;
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
import gdsc.binaryho.imhere.mock.fakerepository.FakeAttendanceHistoryCacheRepository;
import gdsc.binaryho.imhere.mock.fakerepository.FakeAttendeeCacheRepository;
import gdsc.binaryho.imhere.mock.fakerepository.FakeOpenLectureCacheRepository;
import gdsc.binaryho.imhere.mock.fakerepository.FakeVerificationCodeRepository;
import gdsc.binaryho.imhere.security.util.AuthenticationHelper;
import gdsc.binaryho.imhere.util.SeoulDateTimeHolder;
import lombok.Builder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestContainer {

    public final AuthService authService;
    public final EmailVerificationService emailVerificationService;
    public final LectureService lectureService;
    public final LecturerAttendanceService lecturerAttendanceService;
    public final StudentAttendanceService studentAttendanceService;
    public final OpenLectureService openLectureService;
    public final EnrollmentService enrollmentService;

    public final OpenLectureCacheRepository openLectureCacheRepository = new FakeOpenLectureCacheRepository();
    public final AttendeeCacheRepository attendeeCacheRepository = new FakeAttendeeCacheRepository();
    public final VerificationCodeRepository verificationCodeRepository = new FakeVerificationCodeRepository();
    private final AttendanceHistoryCacheRepository attendanceHistoryCacheRepository = new FakeAttendanceHistoryCacheRepository();

    public boolean isMailSent = false;
    private final MailSender mailSender = (recipient, verificationCode) -> isMailSent = true;
    private final AuthenticationHelper authenticationHelper = new AuthenticationHelper();
    private final SeoulDateTimeHolder seoulDateTimeHolder = new FixedSeoulTimeHolder();

    public final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    public final EmailFormValidator emailFormValidator = new EmailFormValidator();

    @Builder
    public TestContainer(
        MemberRepository memberRepository,
        LectureRepository lectureRepository,
        EnrollmentInfoRepository enrollmentInfoRepository,
        AttendanceRepository attendanceRepository,
        ApplicationEventPublisher applicationEventPublisher) {

        /* EmailVerificationService 초기화 */
        emailVerificationService = new EmailVerificationService(mailSender, emailFormValidator,
            verificationCodeRepository);

        /* AuthService 초기화 */
        authService = new AuthService(
            emailVerificationService, memberRepository, bCryptPasswordEncoder
        );

        /* OpenLectureService 초기화 */
        openLectureService = new OpenLectureService(openLectureCacheRepository);

        enrollmentService = new EnrollmentService(
            authenticationHelper, openLectureService, lectureRepository, enrollmentInfoRepository,
            applicationEventPublisher
        );

        /* Attendance service 초기화 */
        lecturerAttendanceService = new LecturerAttendanceService(
            attendanceRepository, lectureRepository, seoulDateTimeHolder, authenticationHelper
        );

        studentAttendanceService = new StudentAttendanceService(openLectureService,
            attendanceRepository, enrollmentInfoRepository, attendanceHistoryCacheRepository,
            applicationEventPublisher, seoulDateTimeHolder, authenticationHelper
        );

        /* LectureService 초기화 */
        lectureService = new LectureService(
            authenticationHelper, lectureRepository, enrollmentInfoRepository,
            openLectureCacheRepository, attendeeCacheRepository, applicationEventPublisher,
            seoulDateTimeHolder
        );
    }
}
