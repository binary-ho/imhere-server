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
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;


@Log4j2
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final AuthenticationHelper authenticationHelper;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final EnrollmentInfoRepository enrollmentInfoRepository;

    @Transactional
    public void enrollStudents(EnrollMentRequestForLecturer enrollMentRequestForLecturer,
        Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        authenticationHelper.verifyRequestMemberLogInMember(lecture.getMember().getId());

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
                log.info("[수강신청 승인 실패] 회원 없음 : " + univId);
                continue;
            }
            members.add(member.get());
        }

        return members;
    }

    @Transactional
    public void requestEnrollment(Long lectureId) {
        Member student = authenticationHelper.getCurrentMember();
        validateDuplicated(student, lectureId);

        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        EnrollmentInfo enrollmentInfo = EnrollmentInfo.createEnrollmentInfo(lecture, student, EnrollmentState.AWAIT);
        enrollmentInfoRepository.save(enrollmentInfo);
    }

    @Transactional
    public void approveStudents(Long lectureId, Long studentId) {
        EnrollmentInfo enrollmentInfo = enrollmentInfoRepository
            .findByMemberIdAndLectureId(studentId, lectureId).orElseThrow();

        authenticationHelper.verifyRequestMemberLogInMember(
            enrollmentInfo.getLecture()
                .getMember()
                .getId()
        );
        enrollmentInfo.setEnrollmentState(EnrollmentState.APPROVAL);

        log.info("[수강신청 승인] 강의 : {} ({}) 학생 : {} ({})"
            , () -> enrollmentInfo.getLecture().getLectureName(), () -> enrollmentInfo.getLecture().getLecturerName()
            , () -> enrollmentInfo.getMember().getUnivId(), () -> enrollmentInfo.getMember().getName());
    }

    @Transactional
    public void rejectStudents(Long lectureId, Long studentId) {
        EnrollmentInfo enrollmentInfo = enrollmentInfoRepository
            .findByMemberIdAndLectureId(studentId, lectureId).orElseThrow();
        authenticationHelper.verifyRequestMemberLogInMember(enrollmentInfo.getLecture().getMember().getId());
        enrollmentInfo.setEnrollmentState(EnrollmentState.REJECTION);

        log.info("[수강신청 거절] 강의 : {} ({}) 학생 : {} ({})"
            , () -> enrollmentInfo.getLecture().getLectureName(), () -> enrollmentInfo.getLecture().getLecturerName()
            , () -> enrollmentInfo.getMember().getUnivId(), () -> enrollmentInfo.getMember().getName());
    }

    public EnrollmentInfoDto getLectureEnrollment(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        authenticationHelper.verifyRequestMemberLogInMember(lecture.getMember().getId());
        List<EnrollmentInfo> enrollmentInfos = enrollmentInfoRepository.findAllByLecture(lecture);
        return EnrollmentInfoDto.createEnrollmentInfoDto(enrollmentInfos);
    }

    private void validateDuplicated(Member student, Long lectureId) {
        Optional<EnrollmentInfo> enrollmentInfo = enrollmentInfoRepository
            .findByMemberIdAndLectureId(student.getId(), lectureId);

        if (enrollmentInfo.isPresent()) {
            EnrollmentInfo enrollment = enrollmentInfo.get();

            log.info("[수강신청 중복] 학생 : {}, 강의 : {} ({})"
                , () -> enrollment.getMember().getUnivId()
                , () -> enrollment.getLecture().getLectureName()
                , () -> enrollment.getLecture().getId());
            throw new DuplicateKeyException("Enrollment Already Exist");
        }
    }
}
