package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.domain.enrollment.EnrollRequest;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureRepository;
import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentService {

    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final EnrollmentInfoRepository enrollmentInfoRepository;

    public EnrollmentService(
        LectureRepository lectureRepository,
        MemberRepository memberRepository,
        EnrollmentInfoRepository enrollmentInfoRepository) {
        this.lectureRepository = lectureRepository;
        this.memberRepository = memberRepository;
        this.enrollmentInfoRepository = enrollmentInfoRepository;
    }

    public void enrollStudents(EnrollRequest enrollRequest) {
        Lecture lecture = lectureRepository.findById(enrollRequest.getLectureId()).orElseThrow();
        List<Member> students = getStudentsByUnivId(enrollRequest.getUnivIds());
        students.forEach(student -> enrollStudent(lecture, student));
    }

    private EnrollmentInfo enrollStudent(Lecture lecture, Member student) {
        EnrollmentInfo enrollmentInfo = EnrollmentInfo.createEnrollmentInfo(lecture, student);
        return enrollmentInfoRepository.save(enrollmentInfo);
    }

    private List<Member> getStudentsByUnivId(List<String> univIds) {
        return univIds.stream()
            .map(univId -> memberRepository.findByUnivId(univId).orElseThrow())
            .collect(Collectors.toList());
    }
}
