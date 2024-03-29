package gdsc.binaryho.imhere.core.enrollment.application;

import static gdsc.binaryho.imhere.mock.fixture.EnrollmentInfoFixture.MOCK_ENROLLMENT_INFO;
import static gdsc.binaryho.imhere.mock.fixture.LectureFixture.MOCK_LECTURE;
import static gdsc.binaryho.imhere.mock.fixture.LectureFixture.MOCK_OPEN_LECTURE;
import static gdsc.binaryho.imhere.mock.fixture.MemberFixture.MOCK_STUDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gdsc.binaryho.imhere.core.auth.exception.RequestMemberIdMismatchException;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.core.enrollment.exception.EnrollmentDuplicatedException;
import gdsc.binaryho.imhere.core.enrollment.exception.EnrollmentNotFoundException;
import gdsc.binaryho.imhere.core.enrollment.infrastructure.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.core.enrollment.model.response.EnrollmentInfoResponse;
import gdsc.binaryho.imhere.core.enrollment.model.response.EnrollmentInfoResponse.StudentInfo;
import gdsc.binaryho.imhere.core.lecture.application.port.OpenLectureCacheRepository;
import gdsc.binaryho.imhere.core.lecture.domain.AttendeeCacheEvent;
import gdsc.binaryho.imhere.core.lecture.domain.OpenLecture;
import gdsc.binaryho.imhere.core.lecture.exception.LectureNotFoundException;
import gdsc.binaryho.imhere.core.lecture.infrastructure.LectureRepository;
import gdsc.binaryho.imhere.mock.TestContainer;
import gdsc.binaryho.imhere.mock.securitycontext.MockSecurityContextMember;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@SpringBootTest
@RecordApplicationEvents
class EnrollmentServiceTest {

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private EnrollmentInfoRepository enrollmentInfoRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ApplicationEvents events;

    private EnrollmentService enrollmentService;
    private OpenLectureCacheRepository openLectureCacheRepository;

    @BeforeEach
    void beforeEachTest() {
        TestContainer testContainer = TestContainer.builder()
            .lectureRepository(lectureRepository)
            .enrollmentInfoRepository(enrollmentInfoRepository)
            .applicationEventPublisher(applicationEventPublisher)
            .build();

        enrollmentService = testContainer.enrollmentService;
        openLectureCacheRepository = testContainer.openLectureCacheRepository;
    }

    @Test
    @MockSecurityContextMember
    void 학생은_수강신청_할_수_있다() {
        // given
        given(enrollmentInfoRepository
            .findByMemberIdAndLectureId(MOCK_STUDENT.getId(), MOCK_LECTURE.getId()))
            .willReturn(Optional.empty());

        given(lectureRepository.findById(MOCK_LECTURE.getId()))
            .willReturn(Optional.of(MOCK_LECTURE));

        // when
        enrollmentService.requestEnrollment(MOCK_LECTURE.getId());

        // then
        verify(enrollmentInfoRepository, times(1)).save(any());
    }

    @Test
    @MockSecurityContextMember
    void 학생이_중복_수강신청_하는_경우_예외를_발생시킨다() {
        // given
        given(enrollmentInfoRepository
            .findByMemberIdAndLectureId(MOCK_STUDENT.getId(), MOCK_LECTURE.getId()))
            .willReturn(Optional.of(MOCK_ENROLLMENT_INFO));

        // when
        // then
        assertThatThrownBy(
            () -> enrollmentService.requestEnrollment(MOCK_LECTURE.getId())
        ).isInstanceOf(EnrollmentDuplicatedException.class);
    }

    @Test
    @MockSecurityContextMember
    void 학생이_존재하지_않는_강의에_수강신청_하는_경우_예외를_발생시킨다() {
        // given
        given(enrollmentInfoRepository
            .findByMemberIdAndLectureId(MOCK_STUDENT.getId(), MOCK_LECTURE.getId()))
            .willReturn(Optional.empty());

        given(lectureRepository.findById(MOCK_LECTURE.getId()))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> enrollmentService.requestEnrollment(MOCK_LECTURE.getId())
        ).isInstanceOf(LectureNotFoundException.class);
    }

    @Test
    @MockSecurityContextMember(id = 2L)
    void 강사는_수강_신청한_학생을_승인할_수_있다() {
        // given
        EnrollmentInfo enrollmentInfo = mock(EnrollmentInfo.class);

        given(enrollmentInfoRepository.findByMemberIdAndLectureId(MOCK_STUDENT.getId(), MOCK_LECTURE.getId()))
            .willReturn(Optional.of(enrollmentInfo));

        given(enrollmentInfo.getLecture())
            .willReturn(MOCK_LECTURE);

        given(enrollmentInfo.getMember())
            .willReturn(MOCK_STUDENT);

        // when
        enrollmentService.approveStudents(MOCK_LECTURE.getId(), MOCK_STUDENT.getId());

        // then
        verify(enrollmentInfo, times(1)).setEnrollmentState(EnrollmentState.APPROVAL);
    }

    @Test
    @MockSecurityContextMember(id = 2L)
    void 강사가_승인하려는_신청이_유효하지_않으면_예외가_발생한다() {
        // given
        Long wrongLectureId = any();
        Long wrongStudentId = any();
        given(enrollmentInfoRepository.findByMemberIdAndLectureId(wrongLectureId, wrongStudentId))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> enrollmentService.approveStudents(MOCK_LECTURE.getId(), MOCK_STUDENT.getId())
        ).isInstanceOf(EnrollmentNotFoundException.class);
    }

    @Test
    @MockSecurityContextMember(id = 3L)
    void 강사가_승인_시도한_강의가_자신의_강의가_아닌_경우_예외가_발생한다() {
        // given
        EnrollmentInfo enrollmentInfo = mock(EnrollmentInfo.class);

        given(enrollmentInfoRepository.findByMemberIdAndLectureId(MOCK_STUDENT.getId(), MOCK_LECTURE.getId()))
            .willReturn(Optional.of(enrollmentInfo));

        given(enrollmentInfo.getLecture())
            .willReturn(MOCK_LECTURE);

        // when
        // then
        assertThatThrownBy(
            () -> enrollmentService.approveStudents(MOCK_LECTURE.getId(), MOCK_STUDENT.getId())
        ).isInstanceOf(RequestMemberIdMismatchException.class);
    }


    @Test
    @MockSecurityContextMember(id = 2L)
    void 강사는_수강_신청한_학생을_거부할_수_있다() {
        // given
        EnrollmentInfo enrollmentInfo = mock(EnrollmentInfo.class);

        given(enrollmentInfoRepository.findByMemberIdAndLectureId(MOCK_STUDENT.getId(), MOCK_LECTURE.getId()))
            .willReturn(Optional.of(enrollmentInfo));

        given(enrollmentInfo.getLecture())
            .willReturn(MOCK_LECTURE);

        given(enrollmentInfo.getMember())
            .willReturn(MOCK_STUDENT);

        // when
        enrollmentService.rejectStudents(MOCK_LECTURE.getId(), MOCK_STUDENT.getId());

        // then
        verify(enrollmentInfo, times(1)).setEnrollmentState(EnrollmentState.REJECTION);
    }

    @Test
    @MockSecurityContextMember(id = 2L)
    void 강사가_학생을_승인할_때_이미_수업이_OPEN_이라면_캐싱한다() {
        // given
        EnrollmentInfo enrollmentInfo = EnrollmentInfo
            .createEnrollmentInfo(MOCK_OPEN_LECTURE, MOCK_STUDENT, EnrollmentState.AWAIT);
        given(enrollmentInfoRepository.findByMemberIdAndLectureId(any(), any()))
            .willReturn(Optional.of(enrollmentInfo));

        OpenLecture openLecture = new OpenLecture(MOCK_OPEN_LECTURE.getId(),
            MOCK_OPEN_LECTURE.getLectureName(), MOCK_OPEN_LECTURE.getLecturerName(), 7777);
        openLectureCacheRepository.cache(openLecture);

        // when
        enrollmentService.approveStudents(MOCK_OPEN_LECTURE.getId(), MOCK_STUDENT.getId());

        // then
        assertThat(events.stream(AttendeeCacheEvent.class).count()).isEqualTo(1);
    }

    @Test
    @MockSecurityContextMember(id = 2L)
    void 강사는_자신이_개설한_강의의_수강신청_리스트를_확인할_수_있다() {
        // given
        given(lectureRepository.findById(MOCK_LECTURE.getId()))
            .willReturn(Optional.of(MOCK_LECTURE));

        EnrollmentInfo enrollmentInfo = EnrollmentInfo
            .createEnrollmentInfo(MOCK_LECTURE, MOCK_STUDENT, EnrollmentState.APPROVAL);

        List<EnrollmentInfo> enrollmentInfos = Collections.singletonList(enrollmentInfo);

        given(enrollmentInfoRepository.findAllByLecture(MOCK_LECTURE))
            .willReturn(enrollmentInfos);

        // when
        EnrollmentInfoResponse response =
            enrollmentService.getLectureEnrollment(MOCK_LECTURE.getId());

        // then
        Long responseLectureId = response.getLectureId();
        List<StudentInfo> responseLectureStudentList = response.getStudentInfos();
        Long enrollStudentId = responseLectureStudentList.get(0).getId();

        assertAll(
            () -> assertThat(responseLectureId).isEqualTo(MOCK_LECTURE.getId()),
            () -> assertThat(enrollStudentId).isEqualTo(MOCK_STUDENT.getId())
        );
    }

    @Test
    @MockSecurityContextMember(id = 2L)
    void 강사는_수강신청이_없는_경우_강의_정보와_함께_빈_수강신청_리스트를_받을_수_있다() {
        // given
        given(lectureRepository.findById(MOCK_LECTURE.getId()))
            .willReturn(Optional.of(MOCK_LECTURE));

        given(enrollmentInfoRepository.findAllByLecture(MOCK_LECTURE))
            .willReturn(Collections.emptyList());

        // when
        EnrollmentInfoResponse response =
            enrollmentService.getLectureEnrollment(MOCK_LECTURE.getId());

        // then
        Long responseLectureId = response.getLectureId();
        List<StudentInfo> responseLectureStudentList = response.getStudentInfos();

        assertAll(
            () -> assertThat(responseLectureId).isEqualTo(MOCK_LECTURE.getId()),
            () -> assertThat(responseLectureStudentList.isEmpty()).isTrue()
        );
    }

    @Test
    @MockSecurityContextMember(id = 2L)
    void 강사가_존재하지_않는_수업에_수강신청_리스트를_요청하는_경우_예외가_발생한다() {
        // given
        given(lectureRepository.findById(MOCK_LECTURE.getId()))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> enrollmentService.getLectureEnrollment(MOCK_LECTURE.getId())
        ).isInstanceOf(LectureNotFoundException.class);
    }

    @Test
    @MockSecurityContextMember(id = 3L)
    void 강사가_다른_강사의_수업에_수강신청_리스트를_요청하는_경우_예외가_발생한다() {
        // given
        given(lectureRepository.findById(MOCK_LECTURE.getId()))
            .willReturn(Optional.of(MOCK_LECTURE));

        // when
        // then
        assertThatThrownBy(
            () -> enrollmentService.getLectureEnrollment(MOCK_LECTURE.getId())
        ).isInstanceOf(RequestMemberIdMismatchException.class);
    }
}
