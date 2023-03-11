package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureRepository;
import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.MemberRepository;
import gdsc.binaryho.imhere.mapper.requests.EnrollRequest;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final EnrollmentInfoRepository enrollmentInfoRepository;

    @Transactional
    public void enrollStudents(EnrollRequest enrollRequest,
        Long lectureId) throws Exception {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        AuthenticationService.verifyRequestMemberLogInMember(lecture.getMember().getId());

        List<Member> students = getStudentsByUnivId(enrollRequest.getUnivIds());
        students.forEach(student -> enrollStudent(lecture, student));
    }

    private void enrollStudent(Lecture lecture, Member student) {
        EnrollmentInfo enrollmentInfo = EnrollmentInfo.createEnrollmentInfo(lecture, student);
        enrollmentInfoRepository.save(enrollmentInfo);
    }

    private List<Member> getStudentsByUnivId(List<String> univIds) {
        return univIds.stream()
            .map(univId -> memberRepository.findByUnivId(univId).orElseThrow())
            .collect(Collectors.toList());
    }
}
