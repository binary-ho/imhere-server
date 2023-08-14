package gdsc.binaryho.imhere.core.lecture.infrastructure;

import gdsc.binaryho.imhere.core.lecture.Lecture;
import gdsc.binaryho.imhere.core.lecture.LectureState;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    Optional<Lecture> findById(Long id);
    List<Lecture> findAllByMemberId(Long id);
    List<Lecture> findAllByLectureStateNot(LectureState lectureState);
    @Query("SELECT e.lecture FROM EnrollmentInfo e WHERE e.member.id = :memberId AND e.enrollmentState = 'APPROVAL' AND e.lecture.lectureState = 'OPEN'")
    List<Lecture> findOpenAndApprovalLecturesByMemberId(@Param("memberId") Long memberId);
}
