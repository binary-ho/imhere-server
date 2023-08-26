package gdsc.binaryho.imhere.core.attendance.application;

import static gdsc.binaryho.imhere.mock.fixture.AttendanceFixture.ACCURACY;
import static gdsc.binaryho.imhere.mock.fixture.AttendanceFixture.ATTENDANCE_NUMBER;
import static gdsc.binaryho.imhere.mock.fixture.AttendanceFixture.DISTANCE;
import static gdsc.binaryho.imhere.mock.fixture.AttendanceFixture.MILLISECONDS;
import static gdsc.binaryho.imhere.mock.fixture.AttendanceFixture.MOCK_ATTENDANCE;
import static gdsc.binaryho.imhere.mock.fixture.EnrollmentInfoFixture.MOCK_ENROLLMENT_INFO;
import static gdsc.binaryho.imhere.mock.fixture.LectureFixture.MOCK_CLOSED_LECTURE;
import static gdsc.binaryho.imhere.mock.fixture.LectureFixture.MOCK_LECTURE;
import static gdsc.binaryho.imhere.mock.fixture.LectureFixture.MOCK_OPEN_LECTURE;
import static gdsc.binaryho.imhere.mock.fixture.MemberFixture.MOCK_STUDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gdsc.binaryho.imhere.core.attendance.exception.AttendanceNumberIncorrectException;
import gdsc.binaryho.imhere.core.attendance.exception.AttendanceTimeExceededException;
import gdsc.binaryho.imhere.core.attendance.infrastructure.AttendanceRepository;
import gdsc.binaryho.imhere.core.attendance.model.request.AttendanceRequest;
import gdsc.binaryho.imhere.core.attendance.model.response.AttendanceResponse;
import gdsc.binaryho.imhere.core.attendance.model.response.AttendanceResponse.AttendanceInfo;
import gdsc.binaryho.imhere.core.auth.exception.RequestMemberIdMismatchException;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.core.enrollment.infrastructure.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.core.lecture.application.OpenLectureService;
import gdsc.binaryho.imhere.core.lecture.application.port.OpenLectureCacheRepository;
import gdsc.binaryho.imhere.core.lecture.domain.OpenLecture;
import gdsc.binaryho.imhere.core.lecture.exception.LectureNotOpenException;
import gdsc.binaryho.imhere.core.lecture.infrastructure.LectureRepository;
import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.mock.TestContainer;
import gdsc.binaryho.imhere.mock.securitycontext.MockSecurityContextMember;
import gdsc.binaryho.imhere.util.SeoulDateTime;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AttendanceServiceTest {

    @Mock
    AttendanceRepository attendanceRepository;
    @Mock
    EnrollmentInfoRepository enrollmentRepository;
    @Mock
    LectureRepository lectureRepository;

    OpenLectureCacheRepository openLectureCacheRepository;

    OpenLectureService openLectureService;
    AttendanceService attendanceService;

    TestContainer testContainer;

    @BeforeEach
    void beforeEachTest() {
        testContainer = TestContainer.builder()
            .enrollmentInfoRepository(enrollmentRepository)
            .attendanceRepository(attendanceRepository)
            .lectureRepository(lectureRepository)
            .build();
        openLectureCacheRepository = testContainer.openLectureCacheRepository;
        attendanceService = testContainer.attendanceService;
        openLectureService = testContainer.openLectureService;
    }

    @Test
    @MockSecurityContextMember
    void 저장된_출석_번호와_학생이_제출한_출석번호가_일치하는_경우_학생은_출석된다() {
        // given
        given(enrollmentRepository
            .findByMemberIdAndLectureIdAndEnrollmentState(any(), eq(MOCK_OPEN_LECTURE.getId()), eq(EnrollmentState.APPROVAL)))
            .willReturn(Optional.of(MOCK_ENROLLMENT_INFO));

        // when
        AttendanceRequest request = new AttendanceRequest(ATTENDANCE_NUMBER, DISTANCE, ACCURACY, MILLISECONDS);

        openLectureCacheRepository.cache(new OpenLecture(MOCK_OPEN_LECTURE.getId(), MOCK_OPEN_LECTURE.getLectureName(),
            MOCK_OPEN_LECTURE.getLecturerName(), ATTENDANCE_NUMBER));

        attendanceService.takeAttendance(request, MOCK_OPEN_LECTURE.getId());

        // then
        verify(attendanceRepository, times(1)).save(any());
    }

    @Test
    @MockSecurityContextMember
    void 학생이_출석을_시도한_수업이_열려있지_않은_경우_예외가_발생한다() {
        // given
        EnrollmentInfo closeLectureEnrollmentInfo = EnrollmentInfo
            .createEnrollmentInfo(MOCK_CLOSED_LECTURE, MOCK_STUDENT, EnrollmentState.APPROVAL);

        given(enrollmentRepository
            .findByMemberIdAndLectureIdAndEnrollmentState(any(), eq(MOCK_CLOSED_LECTURE.getId()), eq(EnrollmentState.APPROVAL)))
            .willReturn(Optional.of(closeLectureEnrollmentInfo));

        // when
        AttendanceRequest request = new AttendanceRequest(ATTENDANCE_NUMBER, DISTANCE, ACCURACY, MILLISECONDS);

        openLectureCacheRepository.cache(new OpenLecture(MOCK_CLOSED_LECTURE.getId(), MOCK_CLOSED_LECTURE.getLectureName(),
            MOCK_CLOSED_LECTURE.getLecturerName(), ATTENDANCE_NUMBER));

        // then
        assertThatThrownBy(
            () -> attendanceService.takeAttendance(request, MOCK_CLOSED_LECTURE.getId()))
            .isInstanceOf(LectureNotOpenException.class);
    }

    @Test
    @MockSecurityContextMember
    void 수업에_저장된_출석_번호가_없는_경우_예외를_발생시킨다() {
        // given
        given(enrollmentRepository
            .findByMemberIdAndLectureIdAndEnrollmentState(any(), eq(MOCK_OPEN_LECTURE.getId()), eq(EnrollmentState.APPROVAL)))
            .willReturn(Optional.of(MOCK_ENROLLMENT_INFO));

        // when
        AttendanceRequest request = new AttendanceRequest(ATTENDANCE_NUMBER, DISTANCE, ACCURACY, MILLISECONDS);
        Integer attendanceNumber = openLectureService.findAttendanceNumber(MOCK_OPEN_LECTURE.getId());

        // then
        assertAll(
            () -> assertThat(attendanceNumber).isEqualTo(null),
            () -> assertThatThrownBy(
                () -> attendanceService.takeAttendance(request, MOCK_OPEN_LECTURE.getId()))
                .isInstanceOf(AttendanceTimeExceededException.class)
        );
    }

    @Test
    @MockSecurityContextMember
    void 저장된_출석_번호와_학생이_제출한_출석번호가_다른_경우_예외를_발생시킨다() {
        // given
        given(enrollmentRepository
            .findByMemberIdAndLectureIdAndEnrollmentState(any(), eq(MOCK_OPEN_LECTURE.getId()), eq(EnrollmentState.APPROVAL)))
            .willReturn(Optional.of(MOCK_ENROLLMENT_INFO));

        // when
        AttendanceRequest request = new AttendanceRequest(ATTENDANCE_NUMBER, DISTANCE, ACCURACY, MILLISECONDS);
        int wrongNumber = ATTENDANCE_NUMBER + 7;
        openLectureCacheRepository.cache(new OpenLecture(MOCK_OPEN_LECTURE.getId(), MOCK_OPEN_LECTURE.getLectureName(),
            MOCK_OPEN_LECTURE.getLecturerName(), wrongNumber));

        // then
        assertThatThrownBy(
            () -> attendanceService.takeAttendance(request, MOCK_OPEN_LECTURE.getId()))
            .isInstanceOf(AttendanceNumberIncorrectException.class);
    }

    @Test
    @MockSecurityContextMember(id = 2L, role = Role.LECTURER)
    void 강사는_자신의_수업의_모든_출석_정보를_가져올_수_있다() {
        // given
        given(attendanceRepository.findAllByLectureId(MOCK_LECTURE.getId()))
            .willReturn(Collections.singletonList(MOCK_ATTENDANCE));

        // when
        AttendanceResponse response = attendanceService.getAttendances(MOCK_LECTURE.getId());

        // then
        AttendanceInfo attendanceInfo = response.getAttendanceInfos().get(0);
        assertAll(
            () -> assertThat(response.getLectureName()).isEqualTo(MOCK_LECTURE.getLectureName()),
            () -> assertThat(response.getLecturerName()).isEqualTo(MOCK_LECTURE.getLecturerName()),
            () -> assertThat(attendanceInfo.getUnivId()).isEqualTo(MOCK_ATTENDANCE.getMember().getUnivId()),
            () -> assertThat(attendanceInfo.getName()).isEqualTo(MOCK_ATTENDANCE.getMember().getName()),
            () -> assertThat(attendanceInfo.getAccuracy()).isEqualTo(MOCK_ATTENDANCE.getAccuracy()),
            () -> assertThat(attendanceInfo.getDistance()).isEqualTo(MOCK_ATTENDANCE.getDistance()),
            () -> assertThat(attendanceInfo.getTimestamp()).isEqualTo(MOCK_ATTENDANCE.getTimestamp())
        );
    }

    @Test
    @MockSecurityContextMember(id = 2L, role = Role.LECTURER)
    void 강사는_자신의_수업의_출석_번호가_없는_경우_빈_응답을_받을_수_있다() {
        // given
        given(attendanceRepository.findAllByLectureId(MOCK_LECTURE.getId()))
            .willReturn(Collections.emptyList());

        given(lectureRepository.findById(MOCK_LECTURE.getId()))
            .willReturn(Optional.of(MOCK_LECTURE));

        // when
        AttendanceResponse response = attendanceService.getAttendances(MOCK_LECTURE.getId());

        // then
        assertAll(
            () -> assertThat(response.getLectureName()).isEqualTo(MOCK_LECTURE.getLectureName()),
            () -> assertThat(response.getLecturerName()).isEqualTo(MOCK_LECTURE.getLecturerName()),
            () -> assertThat(response.getAttendanceInfos().isEmpty()).isTrue()
        );
    }

    @Test
    @MockSecurityContextMember(id = 3L, role = Role.LECTURER)
    void 강사가_자신의_수업이_아닌_수업의_출석_정보를_요청하는_경우_예외가_발생한다() {
        // given
        given(attendanceRepository.findAllByLectureId(MOCK_LECTURE.getId()))
            .willReturn(Collections.singletonList(MOCK_ATTENDANCE));

        // when
        // then
        assertThatThrownBy(
            () -> attendanceService.getAttendances(MOCK_LECTURE.getId()))
            .isInstanceOf(RequestMemberIdMismatchException.class);
    }

    @Test
    void 출석_번호를_강의_아이디와_함께_저장할_수_있다() {
        // given
        // when
        openLectureCacheRepository.cache(new OpenLecture(MOCK_LECTURE.getId(), MOCK_LECTURE.getLectureName(),
            MOCK_LECTURE.getLecturerName(), ATTENDANCE_NUMBER));

        // then
        assertThat(openLectureService.findAttendanceNumber(MOCK_LECTURE.getId()))
            .isEqualTo(ATTENDANCE_NUMBER);
    }

    @Test
    @MockSecurityContextMember(id = 2L, role = Role.LECTURER)
    void 강사는_지정_날짜의_출석_정보를_가져올_수_있다() {
        // given
        LocalDateTime dayLocalDateTime = SeoulDateTime.from(MILLISECONDS)
            .withHour(0).withMinute(0).withSecond(0);

        // 위에서 구한 LocalDateTime 이용
        given(attendanceRepository
            .findByLectureIdAndTimestampBetween(MOCK_LECTURE.getId(), dayLocalDateTime, dayLocalDateTime.plusDays(1)))
            .willReturn(Collections.singletonList(MOCK_ATTENDANCE));

        // when
        AttendanceResponse response = attendanceService.getDayAttendances(MOCK_LECTURE.getId(), MILLISECONDS);

        // then
        AttendanceInfo attendanceInfo = response.getAttendanceInfos().get(0);
        assertAll(
            () -> assertThat(response.getLectureName()).isEqualTo(MOCK_LECTURE.getLectureName()),
            () -> assertThat(response.getLecturerName()).isEqualTo(MOCK_LECTURE.getLecturerName()),
            () -> assertThat(attendanceInfo.getUnivId()).isEqualTo(MOCK_ATTENDANCE.getMember().getUnivId()),
            () -> assertThat(attendanceInfo.getName()).isEqualTo(MOCK_ATTENDANCE.getMember().getName()),
            () -> assertThat(attendanceInfo.getAccuracy()).isEqualTo(MOCK_ATTENDANCE.getAccuracy()),
            () -> assertThat(attendanceInfo.getDistance()).isEqualTo(MOCK_ATTENDANCE.getDistance()),
            () -> assertThat(attendanceInfo.getTimestamp()).isEqualTo(MOCK_ATTENDANCE.getTimestamp())
        );
    }
}
