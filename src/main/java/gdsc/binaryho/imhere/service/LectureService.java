package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureCreateRequest;
import gdsc.binaryho.imhere.domain.lecture.LectureRepository;
import gdsc.binaryho.imhere.domain.lecture.LectureState;
import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.MemberRepository;
import gdsc.binaryho.imhere.domain.member.Role;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class LectureService {

    private final LectureRepository lectureRepository;
    private final EnrollmentInfoRepository enrollmentInfoRepository;
    private final MemberRepository memberRepository;

    public LectureService(LectureRepository lectureRepository,
        EnrollmentInfoRepository enrollmentInfoRepository,
        MemberRepository memberRepository) {
        this.lectureRepository = lectureRepository;
        this.enrollmentInfoRepository = enrollmentInfoRepository;
        this.memberRepository = memberRepository;
    }

    public void createLecture(LectureCreateRequest request) {
        Member lecturer = memberRepository.findById(request.getLecturerId()).orElseThrow();
        if (!lecturer.hasRole(Role.LECTURER)) {
            /* TODO: Exception 만들어서 대체 */
            throw new IllegalArgumentException();
        }

        Lecture newLecture = Lecture.createLecture(lecturer, request.getLectureName());
        lectureRepository.save(newLecture);
    }

    public List<Lecture> getStudentLectures(Long studentId) {
        List<EnrollmentInfo> enrollmentInfos = enrollmentInfoRepository.findAllByMemberId(studentId);
        return getLectures(enrollmentInfos);
    }

    public List<Lecture> getStudentOpenLectures(Long studentId) {
        List<EnrollmentInfo> enrollmentInfos = enrollmentInfoRepository.findAllByMemberIdAndLecture_LectureState(studentId, LectureState.OPEN);
        return getLectures(enrollmentInfos);
    }

    private List<Lecture> getLectures(List<EnrollmentInfo> enrollmentInfos) {
        return enrollmentInfos.stream()
            .map(EnrollmentInfo::getLecture)
            .collect(Collectors.toList());
    }
}
