package gdsc.binaryho.imhere.core.lecture.application;

import static gdsc.binaryho.imhere.mock.fixture.LectureFixture.MOCK_LECTURE;
import static gdsc.binaryho.imhere.mock.fixture.MemberFixture.MOCK_STUDENT;
import static org.assertj.core.api.Assertions.assertThat;

import gdsc.binaryho.imhere.core.lecture.application.port.AttendeeCacheRepository;
import gdsc.binaryho.imhere.core.lecture.application.port.OpenLectureCacheRepository;
import gdsc.binaryho.imhere.core.lecture.domain.AttendeeCacheEvent;
import gdsc.binaryho.imhere.core.lecture.model.StudentIds;
import gdsc.binaryho.imhere.mock.TestContainer;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AttendeeCacheServiceTest {

    AttendeeCacheRepository attendeeCacheRepository;
    OpenLectureCacheRepository openLectureCacheRepository;
    OpenLectureService openLectureService;

    @BeforeEach
    void beforeEach() {
        TestContainer testContainer = TestContainer.builder().build();
        attendeeCacheRepository = testContainer.attendeeCacheRepository;
        openLectureCacheRepository = testContainer.openLectureCacheRepository;
        openLectureService = new OpenLectureService(
            openLectureCacheRepository, attendeeCacheRepository);
    }

    @Test
    void Attendee_정보를_저장할_수_있다() {
        // given
        StudentIds studentIds = new StudentIds(MOCK_STUDENT.getId());
        openLectureService.cache(new AttendeeCacheEvent(MOCK_LECTURE.getId(), studentIds));

        // when
        Set<Long> lectureIds = attendeeCacheRepository.findAllAttendLectureId(MOCK_STUDENT.getId());

        // then
        assertThat(lectureIds.stream().findAny().get()).isEqualTo(MOCK_LECTURE.getId());
    }
}
