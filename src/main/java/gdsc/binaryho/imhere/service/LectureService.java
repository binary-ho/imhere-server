package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureRepository;
import gdsc.binaryho.imhere.domain.lecture.LectureState;
import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.Role;
import gdsc.binaryho.imhere.mapper.requests.LectureCreateRequest;
import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final static Integer RANDOM_NUMBER_START = 100;
    private final static Integer RANDOM_NUMBER_END = 1000;

    private final LectureRepository lectureRepository;
    private final EnrollmentInfoRepository enrollmentInfoRepository;
    private final AuthenticationService authenticationService;

    @Transactional
    public void createLecture(LectureCreateRequest request) throws NoSuchObjectException {
        Member lecturer = AuthenticationService.getCurrentMember();
        if (!lecturer.hasRole(Role.LECTURER)) {
            /* TODO: Exception 만들어서 대체 */
            throw new IllegalArgumentException();
        }

        Lecture newLecture = Lecture.createLecture(lecturer, request.getLectureName());
        lectureRepository.save(newLecture);
    }

    public List<Lecture> getStudentLectures() throws NoSuchObjectException {
        Member currentStudent = AuthenticationService.getCurrentMember();
        List<EnrollmentInfo> enrollmentInfos = enrollmentInfoRepository.findAllByMemberId(currentStudent.getId());
        return getLectures(enrollmentInfos);
    }

    public List<Lecture> getStudentOpenLectures() throws NoSuchObjectException {
        Member currentStudent = AuthenticationService.getCurrentMember();
        List<EnrollmentInfo> enrollmentInfos = enrollmentInfoRepository.findAllByMemberIdAndLecture_LectureState(currentStudent.getId(), LectureState.OPEN);
        return getLectures(enrollmentInfos);
    }

    private List<Lecture> getLectures(List<EnrollmentInfo> enrollmentInfos) {
        return enrollmentInfos.stream()
            .map(EnrollmentInfo::getLecture)
            .collect(Collectors.toList());
    }

    public List<Lecture> getOwnLectures() throws NoSuchObjectException {
        Member currentLecturer = AuthenticationService.getCurrentMember();
        return lectureRepository.findAllByMemberId(currentLecturer.getId());
    }

    @Transactional
    public int openLectureAndGetAttendanceNumber(Long lectureId) throws Exception {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        AuthenticationService.verifyRequestMemberLogInMember(lecture.getMember().getId());

        lecture.setLectureState(LectureState.OPEN);

        int attendanceName = generateRandomNumber();
        lecture.setAttendanceNumber(attendanceName);

        return attendanceName;
    }

    @Transactional
    public void closeLecture(Long lectureId) throws Exception {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        AuthenticationService.verifyRequestMemberLogInMember(lecture.getMember().getId());

        lecture.setLectureState(LectureState.CLOSED);
    }

    private int generateRandomNumber() {
        int rangeSize = RANDOM_NUMBER_END - RANDOM_NUMBER_START + 1;
        return (int) (Math.random() * (rangeSize)) + RANDOM_NUMBER_START;
    }
}
