package gdsc.binaryho.imhere.domain.lecture;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    Optional<Lecture> findById(Long id);
    List<Lecture> findAllByMemberId(Long id);
    List<Lecture> findAllByLectureStateNot(LectureState lectureState);
}
