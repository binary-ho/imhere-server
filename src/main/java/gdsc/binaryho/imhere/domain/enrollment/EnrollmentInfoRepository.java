package gdsc.binaryho.imhere.domain.enrollment;

import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureState;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentInfoRepository extends JpaRepository<EnrollmentInfo, Long> {

    List<EnrollmentInfo> findAllByMemberIdAndEnrollmentState(Long member_id, EnrollmentState enrollmentState);
    List<EnrollmentInfo> findAllByMemberIdAndLecture_LectureStateAndEnrollmentState(Long memberId, LectureState lectureState, EnrollmentState enrollmentState);
    List<EnrollmentInfo> findAllByLecture(Lecture lecture);
    Optional<EnrollmentInfo> findByMemberIdAndLectureIdAndEnrollmentState(Long memberId, Long lectureId, EnrollmentState enrollmentState);
}
