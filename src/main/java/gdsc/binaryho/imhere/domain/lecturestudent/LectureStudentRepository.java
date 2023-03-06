package gdsc.binaryho.imhere.domain.lecturestudent;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureStudentRepository extends JpaRepository<LectureStudent, Long> {

    List<LectureStudent> findAllByMemberId(Long memberId);
}
