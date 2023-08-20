package gdsc.binaryho.imhere.core.lecture.application.port;

import gdsc.binaryho.imhere.core.lecture.Lecture;
import gdsc.binaryho.imhere.core.member.Member;
import java.util.List;
import java.util.Set;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface LectureStudentRepository {

    @Transactional(propagation = Propagation.NESTED)
    void saveLectureStudents(Lecture lecture, List<Member> students);

    Set<Long> findLectureIdByStudentId(Long studentId);
}
