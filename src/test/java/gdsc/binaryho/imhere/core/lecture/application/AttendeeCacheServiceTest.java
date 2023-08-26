package gdsc.binaryho.imhere.core.lecture.application;

import static gdsc.binaryho.imhere.fixture.LectureFixture.MOCK_LECTURE;
import static gdsc.binaryho.imhere.fixture.MemberFixture.MOCK_STUDENT;
import static org.assertj.core.api.Assertions.assertThat;

import gdsc.binaryho.imhere.core.lecture.application.port.AttendeeCacheRepository;
import gdsc.binaryho.imhere.core.lecture.domain.AttendeeCacheEvent;
import gdsc.binaryho.imhere.core.lecture.model.StudentIds;
import gdsc.binaryho.imhere.mock.TestContainer;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AttendeeCacheServiceTest {

    AttendeeCacheRepository attendeeCacheRepository;
    AttendeeCacheService attendeeCacheService;

    @BeforeEach
    void beforeEach() {
        TestContainer testContainer = TestContainer.builder().build();
        attendeeCacheRepository = testContainer.attendeeCacheRepository;
        attendeeCacheService = new AttendeeCacheService(attendeeCacheRepository);
    }

    @Test
    void Attendee_정보를_저장할_수_있다() {
        // given
        StudentIds studentIds = new StudentIds(MOCK_STUDENT.getId());
        attendeeCacheService.cache(new AttendeeCacheEvent(MOCK_LECTURE.getId(), studentIds));

        // when
        Set<Long> lectureIds = attendeeCacheRepository.findAllAttendLectureId(MOCK_STUDENT.getId());

        // then
        assertThat(lectureIds.stream().findAny().get()).isEqualTo(MOCK_LECTURE.getId());
    }
}
