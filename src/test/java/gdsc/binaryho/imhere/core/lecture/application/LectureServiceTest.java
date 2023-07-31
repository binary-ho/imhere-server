package gdsc.binaryho.imhere.core.lecture.application;

import static gdsc.binaryho.imhere.fixture.EnrollmentInfoFixture.ENROLLMENT_INFO;
import static gdsc.binaryho.imhere.fixture.LectureFixture.LECTURE;
import static gdsc.binaryho.imhere.fixture.LectureFixture.OPEN_LECTURE;
import static gdsc.binaryho.imhere.fixture.MemberFixture.LECTURER;
import static gdsc.binaryho.imhere.fixture.MemberFixture.LECTURER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gdsc.binaryho.imhere.core.attendance.application.port.AttendanceNumberRepository;
import gdsc.binaryho.imhere.core.auth.exception.RequestMemberIdMismatchException;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.core.enrollment.infrastructure.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.core.lecture.Lecture;
import gdsc.binaryho.imhere.core.lecture.LectureState;
import gdsc.binaryho.imhere.core.lecture.exception.LectureNotFoundException;
import gdsc.binaryho.imhere.core.lecture.infrastructure.LectureRepository;
import gdsc.binaryho.imhere.core.lecture.model.request.LectureCreateRequest;
import gdsc.binaryho.imhere.core.lecture.model.response.AttendanceNumberResponse;
import gdsc.binaryho.imhere.core.lecture.model.response.LectureResponse;
import gdsc.binaryho.imhere.core.lecture.model.response.LectureResponse.LectureInfo;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.fixture.MemberFixture;
import gdsc.binaryho.imhere.mock.TestContainer;
import gdsc.binaryho.imhere.mock.securitycontext.MockSecurityContextMember;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LectureServiceTest {

    private static final String LECTURE_NAME = "GDSC 기초 웹 스터디";

    @Mock
    private LectureRepository lectureRepository;
    @Mock
    private EnrollmentInfoRepository enrollmentInfoRepository;

    private LectureService lectureService;

    TestContainer testContainer;

    @BeforeEach
    void initServices() {
        testContainer = TestContainer.builder()
            .lectureRepository(lectureRepository)
            .enrollmentInfoRepository(enrollmentInfoRepository)
            .build();

        lectureService = testContainer.lectureService;
    }

    @Test
    @MockSecurityContextMember(role = Role.LECTURER)
    void 강사는_강의를_만들_수_있다() {
        // given
        LectureCreateRequest lectureCreateRequest = new LectureCreateRequest(LECTURE_NAME);

        // when
        lectureService.createLecture(lectureCreateRequest);

        // then
        verify(lectureRepository, times(1)).save(any());
    }

    @Test
    @MockSecurityContextMember
    void 학생은_수강중인_강의_리스트를_가져올_수_있다() {
        // given
        EnrollmentInfo enrollmentInfo0 = EnrollmentInfo
            .createEnrollmentInfo(LECTURE, MemberFixture.STUDENT, EnrollmentState.APPROVAL);

        EnrollmentInfo enrollmentInfo1 = EnrollmentInfo
            .createEnrollmentInfo(OPEN_LECTURE, MemberFixture.STUDENT, EnrollmentState.APPROVAL);

        List<EnrollmentInfo> enrollmentInfos = List.of(enrollmentInfo0, enrollmentInfo1);

        given(enrollmentInfoRepository
            .findAllByMemberIdAndEnrollmentState(any(), eq(EnrollmentState.APPROVAL)))
            .willReturn(enrollmentInfos);
        Long expectedLectureId0 = enrollmentInfo0.getLecture().getId();
        Long expectedLectureId1 = enrollmentInfo1.getLecture().getId();

        // when
        LectureResponse lectureResponse = lectureService.getStudentLectures();

        // then
        List<LectureInfo> lectureInfos = lectureResponse.getLectureInfos();
        Long actualLectureId0 = lectureInfos.get(0).getLectureId();
        Long actualLectureId1 = lectureInfos.get(1).getLectureId();
        assertAll(
            () -> assertThat(lectureInfos.size()).isEqualTo(enrollmentInfos.size()),
            () -> assertThat(actualLectureId0).isEqualTo(expectedLectureId0),
            () -> assertThat(actualLectureId1).isEqualTo(expectedLectureId1)
        );
    }

    @Test
    @MockSecurityContextMember
    void 학생은_수강중인_강의_중_Open된_강의를_가져올_수_있다() {
        // given
        EnrollmentInfo enrollmentInfo = EnrollmentInfo
            .createEnrollmentInfo(OPEN_LECTURE, MemberFixture.STUDENT, EnrollmentState.APPROVAL);

        given(enrollmentInfoRepository
            .findAllByMemberIdAndLecture_LectureStateAndEnrollmentState(
                1L, LectureState.OPEN, EnrollmentState.APPROVAL))
            .willReturn(List.of(enrollmentInfo));

        Long expectedOpenLectureId = enrollmentInfo.getLecture().getId();

        // when
        LectureResponse lectureResponse = lectureService.getStudentOpenLectures();

        // then
        Long actualOpenLectureId = lectureResponse.getLectureInfos().get(0).getLectureId();
        assertThat(actualOpenLectureId).isEqualTo(expectedOpenLectureId);
    }

    @Test
    @MockSecurityContextMember(id = 2L, role = Role.LECTURER)
    void 강사는_자신이_개설한_강의_리스트를_가져올_수_있다() {
        // given
        given(lectureRepository.findAllByMemberId(LECTURER_ID))
            .willReturn(List.of(LECTURE));

        given(enrollmentInfoRepository
            .findAllByLectureAndEnrollmentState(LECTURE, EnrollmentState.APPROVAL))
            .willReturn(List.of(ENROLLMENT_INFO));
        String expectedLecturerName = LECTURE.getLecturerName();

        // when
        LectureResponse lectureResponse = lectureService.getOwnedLectures();

        // then
        String actualLecturerName = lectureResponse.getLectureInfos().get(0).getLecturerName();
        assertThat(expectedLecturerName).isEqualTo(actualLecturerName);
    }

    @Test
    @MockSecurityContextMember(id = 2L, role = Role.LECTURER)
    void 강사는_자신의_강의를_열_수_있다() {
        // given
        Lecture mockLecture = mock(Lecture.class);
        given(mockLecture.getMember())
            .willReturn(LECTURER);

        given(lectureRepository.findById(LECTURER.getId()))
            .willReturn(Optional.of(mockLecture));

        // when
        lectureService.openLectureAndGenerateAttendanceNumber(LECTURER.getId());

        // then
        verify(mockLecture, times(1)).setLectureState(LectureState.OPEN);
    }

    @Test
    @MockSecurityContextMember(id = 2L, role = Role.LECTURER)
    void 강사가_강의를_열_때_출석_번호가_발급되고_저장된다() {
        // given
        given(lectureRepository.findById(LECTURER.getId()))
            .willReturn(Optional.of(LECTURE));

        // when
        AttendanceNumberResponse response = lectureService.openLectureAndGenerateAttendanceNumber(LECTURER.getId());
        int generatedAttendanceNumber = response.getAttendanceNumber();

        // then
        AttendanceNumberRepository attendanceNumberRepository =
            testContainer.attendanceNumberRepository;
        Integer savedAttendanceNumber = attendanceNumberRepository.getByLectureId(LECTURE.getId());

        assertThat(generatedAttendanceNumber).isEqualTo(savedAttendanceNumber);
    }

    @Test
    @MockSecurityContextMember(id = 2L, role = Role.LECTURER)
    void 강사는_자신의_강의를_닫을_수_있다() {
        // given
        Lecture mockLecture = mock(Lecture.class);
        given(mockLecture.getMember())
            .willReturn(LECTURER);

        given(lectureRepository.findById(LECTURER.getId()))
            .willReturn(Optional.of(mockLecture));

        // when
        lectureService.closeLecture(LECTURER.getId());

        // then
        verify(mockLecture, times(1)).setLectureState(LectureState.CLOSED);
    }

    @Test
    @MockSecurityContextMember(id = 2L, role = Role.LECTURER)
    void 강사가_강의를_열거나_닫을때_요청한_강의가_존재하지_않는다면_예외를_발생시킨다() {
        // given
        Lecture mockLecture = mock(Lecture.class);
        given(mockLecture.getMember())
            .willReturn(LECTURER);

        Long lectureId = LECTURE.getId();
        given(lectureRepository.findById(lectureId))
            .willReturn(Optional.of(mockLecture));

        // when
        long noExistentLectureId = lectureId + 7777L;

        // then
        assertAll(
            () -> assertThatThrownBy(() ->
                lectureService.openLectureAndGenerateAttendanceNumber(noExistentLectureId))
                .isInstanceOf(LectureNotFoundException.class),

            () -> assertThatThrownBy(() ->
                lectureService.closeLecture(noExistentLectureId))
                .isInstanceOf(LectureNotFoundException.class)
        );
    }

    @Test
    @MockSecurityContextMember(id = 2L, role = Role.LECTURER)
    void 강사가_다른_강사의_강의를_열거나_닫기를_시도하는_경우_예외를_발생시킨다() {
        // given
        Member anotherLecturer =
            Member.createMember("다른 강사", "다른 강사", "다른 강사", Role.LECTURER);
        anotherLecturer.setId(7777L);

        Lecture anotherLecturerLecture = mock(Lecture.class);
        given(anotherLecturerLecture.getMember())
            .willReturn(anotherLecturer);

        given(lectureRepository.findById(anotherLecturerLecture.getId()))
            .willReturn(Optional.of(anotherLecturerLecture));

        // when
        // then
        assertAll(
            () -> assertThatThrownBy(() ->
                lectureService.openLectureAndGenerateAttendanceNumber(anotherLecturerLecture.getId()))
                .isInstanceOf(RequestMemberIdMismatchException.class),

            () -> assertThatThrownBy(() ->
                lectureService.closeLecture(anotherLecturerLecture.getId()))
                .isInstanceOf(RequestMemberIdMismatchException.class)
        );
    }
}
