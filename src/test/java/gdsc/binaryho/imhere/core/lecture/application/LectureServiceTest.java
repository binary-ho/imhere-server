package gdsc.binaryho.imhere.core.lecture.application;

import static gdsc.binaryho.imhere.mock.fixture.AttendanceFixture.ATTENDANCE_NUMBER;
import static gdsc.binaryho.imhere.mock.fixture.EnrollmentInfoFixture.MOCK_ENROLLMENT_INFO;
import static gdsc.binaryho.imhere.mock.fixture.LectureFixture.MOCK_LECTURE;
import static gdsc.binaryho.imhere.mock.fixture.LectureFixture.MOCK_OPEN_LECTURE;
import static gdsc.binaryho.imhere.mock.fixture.MemberFixture.LECTURER_ID;
import static gdsc.binaryho.imhere.mock.fixture.MemberFixture.MOCK_LECTURER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gdsc.binaryho.imhere.core.auth.exception.RequestMemberIdMismatchException;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.core.enrollment.infrastructure.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.core.lecture.LectureState;
import gdsc.binaryho.imhere.core.lecture.application.port.AttendeeCacheRepository;
import gdsc.binaryho.imhere.core.lecture.application.port.OpenLectureCacheRepository;
import gdsc.binaryho.imhere.core.lecture.domain.AttendeeCacheEvent;
import gdsc.binaryho.imhere.core.lecture.domain.Lecture;
import gdsc.binaryho.imhere.core.lecture.domain.OpenLecture;
import gdsc.binaryho.imhere.core.lecture.exception.LectureNotFoundException;
import gdsc.binaryho.imhere.core.lecture.infrastructure.LectureRepository;
import gdsc.binaryho.imhere.core.lecture.model.LectureInfo;
import gdsc.binaryho.imhere.core.lecture.model.StudentIds;
import gdsc.binaryho.imhere.core.lecture.model.request.LectureCreateRequest;
import gdsc.binaryho.imhere.core.lecture.model.response.AttendanceNumberResponse;
import gdsc.binaryho.imhere.core.lecture.model.response.LectureResponse;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.mock.FixedSeoulTimeHolder;
import gdsc.binaryho.imhere.mock.TestContainer;
import gdsc.binaryho.imhere.mock.fixture.MemberFixture;
import gdsc.binaryho.imhere.mock.securitycontext.MockSecurityContextMember;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@SpringBootTest
@RecordApplicationEvents
class LectureServiceTest {

    private static final String LECTURE_NAME = "GDSC 기초 웹 스터디";

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private EnrollmentInfoRepository enrollmentInfoRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ApplicationEvents events;

    private LectureService lectureService;
    private OpenLectureCacheRepository openLectureCacheRepository;
    private AttendeeCacheRepository attendeeCacheRepository;

    @BeforeEach
    void initServices() {
        TestContainer testContainer = TestContainer.builder()
            .lectureRepository(lectureRepository)
            .enrollmentInfoRepository(enrollmentInfoRepository)
            .applicationEventPublisher(applicationEventPublisher)
            .build();

        lectureService = testContainer.lectureService;
        openLectureCacheRepository = testContainer.openLectureCacheRepository;
        attendeeCacheRepository = testContainer.attendeeCacheRepository;
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
    @MockSecurityContextMember(role = Role.LECTURER)
    void 강의_생성시_lastOpeningTime이_현재_시간으로_설정된다() {
        // given
        LectureCreateRequest lectureCreateRequest = new LectureCreateRequest(LECTURE_NAME);

        // when
        lectureService.createLecture(lectureCreateRequest);

        // then
        ArgumentCaptor<Lecture> captor = ArgumentCaptor.forClass(Lecture.class);

        assertAll(
            () -> verify(lectureRepository, times(1)).save(captor.capture()),
            () -> assertThat(captor.getValue().getLastOpeningTime()).isEqualTo(FixedSeoulTimeHolder.FIXED_LOCAL_DATE_TIME)
        );
    }

    @Test
    @MockSecurityContextMember
    void 학생은_수강중인_강의_리스트를_가져올_수_있다() {
        // given
        EnrollmentInfo enrollmentInfo0 = EnrollmentInfo
            .createEnrollmentInfo(MOCK_LECTURE, MemberFixture.MOCK_STUDENT, EnrollmentState.APPROVAL);

        EnrollmentInfo enrollmentInfo1 = EnrollmentInfo
            .createEnrollmentInfo(MOCK_OPEN_LECTURE, MemberFixture.MOCK_STUDENT, EnrollmentState.APPROVAL);

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
            .createEnrollmentInfo(MOCK_OPEN_LECTURE, MemberFixture.MOCK_STUDENT, EnrollmentState.APPROVAL);

        given(lectureRepository.findOpenAndApprovalLecturesByMemberId(any()))
            .willReturn(Collections.singletonList(MOCK_OPEN_LECTURE));

        Long expectedOpenLectureId = enrollmentInfo.getLecture().getId();

        // when
        LectureResponse lectureResponse = lectureService.getStudentOpenLectures();

        // then
        Long actualOpenLectureId = lectureResponse.getLectureInfos().get(0).getLectureId();
        assertThat(actualOpenLectureId).isEqualTo(expectedOpenLectureId);
    }

    @Test
    @MockSecurityContextMember
    void 학생은_수강중인_강의_중_Open된_강의를_찾을_때_캐싱된_데이터를_먼저_확인한다() {
        // given
        OpenLecture openLecture = new OpenLecture(MOCK_OPEN_LECTURE.getId(),
            MOCK_OPEN_LECTURE.getLectureName(), MOCK_OPEN_LECTURE.getLecturerName(), ATTENDANCE_NUMBER);

        openLectureCacheRepository.cache(openLecture);
        attendeeCacheRepository.cache(MOCK_OPEN_LECTURE.getId(), new StudentIds(1L));


        // when
        LectureResponse lectureResponse = lectureService.getStudentOpenLectures();

        // then
        LectureInfo lectureInfo = lectureResponse.getLectureInfos().stream().findAny().get();

        assertAll(
            () -> assertThat(openLecture.getId()).isEqualTo(lectureInfo.getLectureId()),
            () -> assertThat(openLecture.getName()).isEqualTo(lectureInfo.getLectureName()),
            () -> assertThat(openLecture.getLecturerName()).isEqualTo(lectureInfo.getLecturerName()),
            () -> assertThat(LectureState.OPEN).isEqualTo(lectureInfo.getLectureState()),
            () -> verify(lectureRepository, never()).findOpenAndApprovalLecturesByMemberId(any())
        );
    }

    @Test
    @MockSecurityContextMember(id = 2L, role = Role.LECTURER)
    void 강사는_자신이_개설한_강의_리스트를_가져올_수_있다() {
        // given
        given(lectureRepository.findAllByMemberId(LECTURER_ID))
            .willReturn(Collections.singletonList(MOCK_LECTURE));

        given(enrollmentInfoRepository
            .findAllApprovedByLectureId(any()))
            .willReturn(Collections.singletonList(MOCK_ENROLLMENT_INFO));
        String expectedLecturerName = MOCK_LECTURE.getLecturerName();

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
        given(mockLecture.getId())
            .willReturn(MOCK_LECTURE.getId());
        given(mockLecture.getMember())
            .willReturn(MOCK_LECTURER);
        given(mockLecture.getLectureName())
            .willReturn(MOCK_LECTURE.getLectureName());
        given(mockLecture.getLecturerName())
            .willReturn(MOCK_LECTURER.getName());

        given(lectureRepository.findById(MOCK_LECTURER.getId()))
            .willReturn(Optional.of(mockLecture));

        // when
        lectureService.openLectureAndGenerateAttendanceNumber(MOCK_LECTURER.getId());

        // then
        verify(mockLecture, times(1)).setLectureState(LectureState.OPEN);
    }

    @Test
    @MockSecurityContextMember(id = 2L, role = Role.LECTURER)
    void 강사가_강의를_열_때_lastOpeningTime_이_현재_시간으로_변경_된다() {
        // given
        Lecture mockLecture = mock(Lecture.class);
        given(mockLecture.getId())
            .willReturn(MOCK_LECTURE.getId());
        given(mockLecture.getMember())
            .willReturn(MOCK_LECTURER);
        given(mockLecture.getLectureName())
            .willReturn(MOCK_LECTURE.getLectureName());
        given(mockLecture.getLecturerName())
            .willReturn(MOCK_LECTURER.getName());

        given(lectureRepository.findById(MOCK_LECTURER.getId()))
            .willReturn(Optional.of(mockLecture));

        // when
        lectureService.openLectureAndGenerateAttendanceNumber(MOCK_LECTURER.getId());

        // then
        verify(mockLecture, times(1)).setLastOpeningTime(FixedSeoulTimeHolder.FIXED_LOCAL_DATE_TIME);
    }

    @Test
    @MockSecurityContextMember(id = 2L, role = Role.LECTURER)
    void 강사가_강의를_열_때_출석_번호가_발급되고_저장된다() {
        // given
        given(lectureRepository.findById(MOCK_LECTURER.getId()))
            .willReturn(Optional.of(MOCK_LECTURE));

        // when
        AttendanceNumberResponse response = lectureService.openLectureAndGenerateAttendanceNumber(
            MOCK_LECTURER.getId());
        int generatedAttendanceNumber = response.getAttendanceNumber();

        // then
        Integer savedAttendanceNumber = openLectureCacheRepository
            .findAttendanceNumber(MOCK_LECTURE.getId());

        assertThat(generatedAttendanceNumber).isEqualTo(savedAttendanceNumber);
    }

    @Test
    @MockSecurityContextMember(id = 2L, role = Role.LECTURER)
    void 강사가_강의를_열_때_강의_정보가_캐싱된다() {
        // given
        given(lectureRepository.findById(MOCK_LECTURER.getId()))
            .willReturn(Optional.of(MOCK_LECTURE));

        // when
        AttendanceNumberResponse response = lectureService.openLectureAndGenerateAttendanceNumber(
            MOCK_LECTURER.getId());

        // then
        Optional<OpenLecture> savedOpenLecture = openLectureCacheRepository
            .find(MOCK_LECTURE.getId());

        assertAll(
            () -> assertThat(savedOpenLecture.isPresent()).isTrue(),
            () -> assertThat(savedOpenLecture.get().getId()).isEqualTo(MOCK_LECTURE.getId()),
            () -> assertThat(savedOpenLecture.get().getName()).isEqualTo(MOCK_LECTURE.getLectureName()),
            () -> assertThat(savedOpenLecture.get().getLecturerName()).isEqualTo(MOCK_LECTURE.getLecturerName()),
            () -> assertThat(savedOpenLecture.get().getAttendanceNumber()).isEqualTo(response.getAttendanceNumber())
        );
    }

    @Test
    @MockSecurityContextMember(id = 2L, role = Role.LECTURER)
    void 강사가_강의를_열면_AttendeeCacheEvent_이벤트가_발행된다() {
        // given
        given(lectureRepository.findById(MOCK_LECTURER.getId()))
            .willReturn(Optional.of(MOCK_LECTURE));

        // when
        lectureService.openLectureAndGenerateAttendanceNumber(MOCK_LECTURER.getId());

        // then
        assertThat(events.stream(AttendeeCacheEvent.class).count()).isEqualTo(1);
    }

    @Test
    @MockSecurityContextMember(id = 2L, role = Role.LECTURER)
    void 강사는_자신의_강의를_닫을_수_있다() {
        // given
        Lecture mockLecture = mock(Lecture.class);
        given(mockLecture.getMember())
            .willReturn(MOCK_LECTURER);

        given(lectureRepository.findById(MOCK_LECTURER.getId()))
            .willReturn(Optional.of(mockLecture));

        // when
        lectureService.closeLecture(MOCK_LECTURER.getId());

        // then
        verify(mockLecture, times(1)).setLectureState(LectureState.CLOSED);
    }

    @Test
    @MockSecurityContextMember(id = 2L, role = Role.LECTURER)
    void 강사가_강의를_열거나_닫을때_요청한_강의가_존재하지_않는다면_예외를_발생시킨다() {
        // given
        Lecture mockLecture = mock(Lecture.class);
        given(mockLecture.getMember())
            .willReturn(MOCK_LECTURER);

        Long lectureId = MOCK_LECTURE.getId();
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
