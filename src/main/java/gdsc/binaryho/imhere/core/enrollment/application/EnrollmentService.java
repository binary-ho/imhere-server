package gdsc.binaryho.imhere.core.enrollment.application;

import gdsc.binaryho.imhere.core.auth.util.AuthenticationHelper;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.core.enrollment.exception.EnrollmentDuplicatedException;
import gdsc.binaryho.imhere.core.enrollment.exception.EnrollmentNotFoundException;
import gdsc.binaryho.imhere.core.enrollment.infrastructure.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.core.enrollment.model.response.EnrollmentInfoResponse;
import gdsc.binaryho.imhere.core.lecture.Lecture;
import gdsc.binaryho.imhere.core.lecture.exception.LectureNotFoundException;
import gdsc.binaryho.imhere.core.lecture.infrastructure.LectureRepository;
import gdsc.binaryho.imhere.core.member.Member;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Log4j2
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final AuthenticationHelper authenticationHelper;
    private final LectureRepository lectureRepository;
    private final EnrollmentInfoRepository enrollmentInfoRepository;

    @Transactional
    public void requestEnrollment(Long lectureId) {
        Member student = authenticationHelper.getCurrentMember();

        validateDuplicated(student, lectureId);

        Lecture lecture = lectureRepository.findById(lectureId)
            .orElseThrow(() -> LectureNotFoundException.EXCEPTION);

        EnrollmentInfo enrollmentInfo = EnrollmentInfo.createEnrollmentInfo(lecture, student, EnrollmentState.AWAIT);
        enrollmentInfoRepository.save(enrollmentInfo);
    }

    @Transactional
    public void approveStudents(Long lectureId, Long studentId) {
        EnrollmentInfo enrollmentInfo = getEnrollmentInfo(lectureId, studentId);
        validateLecturerOwnLecture(enrollmentInfo.getLecture());

        enrollmentInfo.setEnrollmentState(EnrollmentState.APPROVAL);

        log.info("[수강신청 승인] 강의 : {} ({}) 학생 : {} ({})"
            , () -> enrollmentInfo.getLecture().getLectureName(), () -> enrollmentInfo.getLecture().getLecturerName()
            , () -> enrollmentInfo.getMember().getUnivId(), () -> enrollmentInfo.getMember().getName());
    }

    private EnrollmentInfo getEnrollmentInfo(Long lectureId, Long studentId) {
        return enrollmentInfoRepository
            .findByMemberIdAndLectureId(studentId, lectureId)
            .orElseThrow(() -> EnrollmentNotFoundException.EXCEPTION);
    }

    @Transactional
    public void rejectStudents(Long lectureId, Long studentId) {
        EnrollmentInfo enrollmentInfo = getEnrollmentInfo(lectureId, studentId);
        validateLecturerOwnLecture(enrollmentInfo.getLecture());

        enrollmentInfo.setEnrollmentState(EnrollmentState.REJECTION);

        log.info("[수강신청 거절] 강의 : {} ({}) 학생 : {} ({})"
            , () -> enrollmentInfo.getLecture().getLectureName(), () -> enrollmentInfo.getLecture().getLecturerName()
            , () -> enrollmentInfo.getMember().getUnivId(), () -> enrollmentInfo.getMember().getName());
    }

    @Transactional(readOnly = true)
    public EnrollmentInfoResponse getLectureEnrollment(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
            .orElseThrow(() -> LectureNotFoundException.EXCEPTION);
        validateLecturerOwnLecture(lecture);

        List<EnrollmentInfo> enrollmentInfos = enrollmentInfoRepository.findAllByLecture(lecture);
        return EnrollmentInfoResponse.createEnrollmentInfoDto(lecture, enrollmentInfos);
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
            throw EnrollmentDuplicatedException.EXCEPTION;
        }
    }

    private void validateLecturerOwnLecture(Lecture lecture) {
        authenticationHelper.verifyRequestMemberLogInMember(
            lecture.getMember().getId()
        );
    }
}
