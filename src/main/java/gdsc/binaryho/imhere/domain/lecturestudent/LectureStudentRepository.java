package gdsc.binaryho.imhere.domain.lecturestudent;

import gdsc.binaryho.imhere.domain.lecture.LectureState;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureStudentRepository extends JpaRepository<LectureStudent, Long> {

    List<LectureStudent> findAllByMemberId(Long memberId);
    List<LectureStudent> findAllByMemberIdAndLecture_LectureState(Long memberId, LectureState lectureState);
}
