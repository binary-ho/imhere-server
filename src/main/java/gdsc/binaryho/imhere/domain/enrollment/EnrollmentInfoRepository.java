package gdsc.binaryho.imhere.domain.enrollment;

import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureState;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentInfoRepository extends JpaRepository<EnrollmentInfo, Long> {

    List<EnrollmentInfo> findAllByMemberId(Long memberId);
    List<EnrollmentInfo> findAllByMemberIdAndLecture_LectureState(Long memberId, LectureState lectureState);
    List<EnrollmentInfo> findAllByLecture(Lecture lecture);
    Optional<EnrollmentInfo> findByMemberIdAndLectureId(Long memberId, Long lectureId);
}
