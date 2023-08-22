package gdsc.binaryho.imhere.core.lecture.application;

import static gdsc.binaryho.imhere.fixture.AttendanceFixture.ATTENDANCE_NUMBER;
import static gdsc.binaryho.imhere.fixture.LectureFixture.LECTURE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gdsc.binaryho.imhere.core.lecture.application.port.OpenLectureCacheRepository;
import gdsc.binaryho.imhere.core.lecture.domain.OpenLecture;
import gdsc.binaryho.imhere.mock.TestContainer;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OpenLectureServiceTest {

    private OpenLectureService openLectureService;
    private OpenLectureCacheRepository openLectureCacheRepository;

    @BeforeEach
    void beforeEach() {
        TestContainer testContainer = TestContainer.builder()
            .build();

        openLectureService = testContainer.openLectureService;
        openLectureCacheRepository = testContainer.openLectureCacheRepository;
    }

    @Test
    void 저장된_OpenLecture를_조회할_수_있다() {
        // given
        OpenLecture openLecture = new OpenLecture(LECTURE.getId(), LECTURE.getLectureName(),
            LECTURE.getLecturerName(), ATTENDANCE_NUMBER);
        openLectureCacheRepository.cache(openLecture);

        // when
        Optional<OpenLecture> actualOpenLecture = openLectureService.find(LECTURE.getId());

        // then
        assertAll(
            () -> assertThat(actualOpenLecture.get().getId()).isEqualTo(LECTURE.getId()),
            () -> assertThat(actualOpenLecture.get().getName()).isEqualTo(LECTURE.getLectureName()),
            () -> assertThat(actualOpenLecture.get().getLecturerName()).isEqualTo(LECTURE.getLecturerName()),
            () -> assertThat(actualOpenLecture.get().getAttendanceNumber()).isEqualTo(ATTENDANCE_NUMBER)
        );
    }

    @Test
    void 저장된_AttendanceNumber를_조회할_수_있다() {
        // given
        OpenLecture openLecture = new OpenLecture(LECTURE.getId(), LECTURE.getLectureName(),
            LECTURE.getLecturerName(), ATTENDANCE_NUMBER);
        openLectureCacheRepository.cache(openLecture);

        // when
        Integer actualAttendanceNumber = openLectureService.findAttendanceNumber(LECTURE.getId());

        // then
        assertThat(actualAttendanceNumber).isEqualTo(ATTENDANCE_NUMBER);
    }
}
