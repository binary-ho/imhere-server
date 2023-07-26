package gdsc.binaryho.imhere.core.attendance;

import static gdsc.binaryho.imhere.fixture.MemberFixture.LECTURER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gdsc.binaryho.imhere.MockMember;
import gdsc.binaryho.imhere.core.attendance.application.AttendanceService;
import gdsc.binaryho.imhere.core.attendance.application.port.AttendanceNumberRepository;
import gdsc.binaryho.imhere.core.attendance.exception.AttendanceNumberIncorrectException;
import gdsc.binaryho.imhere.core.attendance.exception.AttendanceTimeExceededException;
import gdsc.binaryho.imhere.core.attendance.infrastructure.AttendanceRepository;
import gdsc.binaryho.imhere.core.attendance.model.request.AttendanceRequest;
import gdsc.binaryho.imhere.core.auth.util.AuthenticationHelper;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.core.enrollment.infrastructure.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.core.lecture.Lecture;
import gdsc.binaryho.imhere.core.lecture.LectureState;
import gdsc.binaryho.imhere.core.lecture.infrastructure.LectureRepository;
import gdsc.binaryho.imhere.fixture.AttendanceFixture;
import gdsc.binaryho.imhere.fixture.MemberFixture;
import gdsc.binaryho.imhere.mock.FakeAttendanceNumberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AttendanceServiceTest {

    private final int ATTENDANCE_NUMBER = AttendanceFixture.ATTENDANCE_NUMBER;
    private final String DISTANCE = AttendanceFixture.DISTANCE;
    private final String ACCURACY = AttendanceFixture.ACCURACY;
    private final long MILLISECONDS = AttendanceFixture.MILLISECONDS;

    private final Lecture OPEN_LECTURE = getMockOpenLecture();
    private final EnrollmentInfo ENROLLMENT_INFO = EnrollmentInfo
        .createEnrollmentInfo(OPEN_LECTURE, MemberFixture.STUDENT, EnrollmentState.APPROVAL);

    @Autowired
    AuthenticationHelper authenticationHelper;
    @Mock
    AttendanceRepository attendanceRepository;
    @Mock
    EnrollmentInfoRepository enrollmentRepository;
    @Autowired
    LectureRepository lectureRepository;

    AttendanceNumberRepository attendanceNumberRepository = new FakeAttendanceNumberRepository();
    AttendanceService attendanceService;

    @BeforeEach
    void saveAllEntities() {
        attendanceService = new AttendanceService(
            authenticationHelper, attendanceRepository, enrollmentRepository, lectureRepository, attendanceNumberRepository);

        // EnrollmentRepository Mocking
        given(enrollmentRepository
            .findByMemberIdAndLectureIdAndEnrollmentState(any(), eq(OPEN_LECTURE.getId()), eq(EnrollmentState.APPROVAL)))
            .willReturn(Optional.of(ENROLLMENT_INFO));
    }

    @Test
    @MockMember
    void 저장된_출석_번호와_학생이_제출한_출석번호가_일치하는_경우_학생은_출석된다() {
        // given
        // when
        AttendanceRequest request = new AttendanceRequest(ATTENDANCE_NUMBER, DISTANCE, ACCURACY, MILLISECONDS);
        attendanceNumberRepository.saveWithLectureIdAsKey(OPEN_LECTURE.getId(), ATTENDANCE_NUMBER);
        attendanceService.takeAttendance(request, OPEN_LECTURE.getId());

        // then
        verify(attendanceRepository, times(1)).save(any());
    }

    @Test
    @MockMember
    void 수업에_저장된_출석_번호가_없는_경우_예외를_발생시킨다() {
        // given
        // when
        AttendanceRequest request = new AttendanceRequest(ATTENDANCE_NUMBER, DISTANCE, ACCURACY, MILLISECONDS);
        Integer attendanceNumber = attendanceNumberRepository.getByLectureId(OPEN_LECTURE.getId());

        // then
        assertAll(
            () -> assertThat(attendanceNumber).isEqualTo(null),
            () -> assertThatThrownBy(
                () -> attendanceService.takeAttendance(request, OPEN_LECTURE.getId()))
                .isInstanceOf(AttendanceTimeExceededException.class)
        );
    }

    @Test
    @MockMember
    void 저장된_출석_번호와_학생이_제출한_출석번호가_다른_경우_예외를_발생시킨다() {
        // given
        // when
        AttendanceRequest request = new AttendanceRequest(ATTENDANCE_NUMBER, DISTANCE, ACCURACY, MILLISECONDS);
        int wrongNumber = ATTENDANCE_NUMBER + 7;
        attendanceNumberRepository.saveWithLectureIdAsKey(OPEN_LECTURE.getId(), wrongNumber);

        // then
        assertThatThrownBy(
            () -> attendanceService.takeAttendance(request, OPEN_LECTURE.getId()))
            .isInstanceOf(AttendanceNumberIncorrectException.class);
    }

    /*@Transactional
    public void saveAttendanceNumber(Long lectureId, int attendanceNumber) {
        attendanceNumberRepository.saveByLectureId(lectureId, attendanceNumber);
    }*/

    @Test
    void 출석_번호를_강의_아이디와_함께_저장할_수_있다() {

    }

    private Lecture getMockOpenLecture() {
        Lecture lecture = Lecture.createLecture(LECTURER, "mockLecture");
        lecture.setId(1L);
        lecture.setLectureState(LectureState.OPEN);
        return lecture;
    }
}
