package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureRepository;
import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.MemberRepository;
import gdsc.binaryho.imhere.mapper.dtos.EnrollmentInfoDto;
import gdsc.binaryho.imhere.mapper.requests.EnrollMentRequestForLecturer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final EnrollmentInfoRepository enrollmentInfoRepository;

    private final Logger logger = LogManager.getLogger(EnrollmentService.class);

    @Transactional
    public void enrollStudents(EnrollMentRequestForLecturer enrollMentRequestForLecturer,
        Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        AuthenticationService.verifyRequestMemberLogInMember(lecture.getMember().getId());

        List<Member> students = getStudentsByUnivId(enrollMentRequestForLecturer.getUnivIds());
        students.forEach(student -> enrollStudent(lecture, student));
    }

    private void enrollStudent(Lecture lecture, Member student) {
        EnrollmentInfo enrollmentInfo = EnrollmentInfo.createEnrollmentInfo(lecture, student, EnrollmentState.APPROVAL);
        enrollmentInfoRepository.save(enrollmentInfo);
    }

    private List<Member> getStudentsByUnivId(List<String> univIds) {
        List<Member> members = new ArrayList<>();
        for (String univId : univIds) {
            Optional<Member> member = memberRepository.findByUnivId(univId);
            if (member.isEmpty()) {
                logger.error("Enrollment Error : there is no such member '" + univId + "' in database");
                continue;
            }
            members.add(member.get());
        }

        return members;
    }

    @Transactional
    public void requestEnrollment(Long lectureId) {
        Member student = AuthenticationService.getCurrentMember();
        validateDuplicated(student, lectureId);

        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        EnrollmentInfo enrollmentInfo = EnrollmentInfo.createEnrollmentInfo(lecture, student, EnrollmentState.AWAIT);
        enrollmentInfoRepository.save(enrollmentInfo);
    }

    @Transactional
    public void approveStudents(Long lectureId, Long studentId) {
        EnrollmentInfo enrollmentInfo = enrollmentInfoRepository
            .findByMemberIdAndLectureId(studentId, lectureId).orElseThrow();

        AuthenticationService.verifyRequestMemberLogInMember(enrollmentInfo.getLecture().getMember().getId());
        enrollmentInfo.setEnrollmentState(EnrollmentState.APPROVAL);
    }

    @Transactional
    public void rejectStudents(Long lectureId, Long studentId) {
        EnrollmentInfo enrollmentInfoAwaited = enrollmentInfoRepository
            .findByMemberIdAndLectureId(studentId, lectureId).orElseThrow();
        AuthenticationService.verifyRequestMemberLogInMember(enrollmentInfoAwaited.getLecture().getMember().getId());
        enrollmentInfoAwaited.setEnrollmentState(EnrollmentState.REJECTION);
    }

    public EnrollmentInfoDto getLectureEnrollment(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        AuthenticationService.verifyRequestMemberLogInMember(lecture.getMember().getId());
        List<EnrollmentInfo> enrollmentInfos = enrollmentInfoRepository.findAllByLecture(lecture);
        return EnrollmentInfoDto.createEnrollmentInfoDto(enrollmentInfos);
    }

    private void validateDuplicated(Member student, Long lectureId) {
        Optional<EnrollmentInfo> enrollmentInfo = enrollmentInfoRepository
            .findByMemberIdAndLectureId(student.getId(), lectureId);

        if (enrollmentInfo.isPresent()) {
            throw new DuplicateKeyException("Enrollment Already Exist");
        }
    }
}
