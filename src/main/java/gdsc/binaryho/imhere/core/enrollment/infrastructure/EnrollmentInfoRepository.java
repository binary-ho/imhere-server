package gdsc.binaryho.imhere.core.enrollment.infrastructure;

import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.core.lecture.Lecture;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EnrollmentInfoRepository extends JpaRepository<EnrollmentInfo, Long> {

    List<EnrollmentInfo> findAllByMemberIdAndEnrollmentState(Long memberId,
        EnrollmentState enrollmentState);

    List<EnrollmentInfo> findAllByLecture(Lecture lecture);

    @Query("SELECT e FROM EnrollmentInfo e WHERE e.lecture.id = :lectureId AND e.enrollmentState = 'APPROVAL'")
    List<EnrollmentInfo> findAllApprovedByLectureId(Long lectureId);

    Optional<EnrollmentInfo> findByMemberIdAndLectureIdAndEnrollmentState(Long memberId,
        Long lectureId, EnrollmentState enrollmentState);

    Optional<EnrollmentInfo> findByMemberIdAndLectureId(Long memberId, Long lectureId);
}
