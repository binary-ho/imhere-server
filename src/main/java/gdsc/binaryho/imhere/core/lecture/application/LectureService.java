package gdsc.binaryho.imhere.core.lecture.application;

import gdsc.binaryho.imhere.core.attendance.application.AttendanceService;
import gdsc.binaryho.imhere.core.auth.util.AuthenticationHelper;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.core.enrollment.infrastructure.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.core.lecture.Lecture;
import gdsc.binaryho.imhere.core.lecture.LectureState;
import gdsc.binaryho.imhere.core.lecture.exception.LectureNotFoundException;
import gdsc.binaryho.imhere.core.lecture.infrastructure.LectureRepository;
import gdsc.binaryho.imhere.core.lecture.model.request.LectureCreateRequest;
import gdsc.binaryho.imhere.core.lecture.model.response.LectureResponse;
import gdsc.binaryho.imhere.core.member.Member;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class LectureService {

    private static final Integer RANDOM_NUMBER_START = 100;
    private static final Integer RANDOM_NUMBER_END = 1000;

    private final AuthenticationHelper authenticationHelper;
    private final AttendanceService attendanceService;
    private final LectureRepository lectureRepository;
    private final EnrollmentInfoRepository enrollmentInfoRepository;

    @Transactional
    public void createLecture(LectureCreateRequest request) {
        Member lecturer = authenticationHelper.getCurrentMember();
        Lecture newLecture = Lecture.createLecture(lecturer, request.getLectureName());
        lectureRepository.save(newLecture);
    }

    @Transactional(readOnly = true)
    public LectureResponse getStudentLectures() {
        Member currentStudent = authenticationHelper.getCurrentMember();
        List<Lecture> studentLectures = findStudentLectures(currentStudent);
        return LectureResponse.createLectureResponseFromLectures(studentLectures);
    }

    private List<Lecture> findStudentLectures(Member currentStudent) {
        List<EnrollmentInfo> enrollmentInfos = enrollmentInfoRepository
            .findAllByMemberIdAndEnrollmentState(
                currentStudent.getId(), EnrollmentState.APPROVAL);
        return enrollmentInfos.stream()
            .map(EnrollmentInfo::getLecture)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LectureResponse getStudentOpenLectures() {
        Member currentStudent = authenticationHelper.getCurrentMember();
        List<Lecture> studentOpenLectures = findStudentOpenLectures(currentStudent);

        return LectureResponse.createLectureResponseFromLectures(studentOpenLectures);
    }

    private List<Lecture> findStudentOpenLectures(Member currentStudent) {
        List<EnrollmentInfo> enrollmentInfos = enrollmentInfoRepository
            .findAllByMemberIdAndLecture_LectureStateAndEnrollmentState(
                currentStudent.getId(), LectureState.OPEN, EnrollmentState.APPROVAL);

        return enrollmentInfos.stream()
            .map(EnrollmentInfo::getLecture)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LectureResponse getOwnedLectures() {
        Member currentLecturer = authenticationHelper.getCurrentMember();
        List<Lecture> lectures = lectureRepository.findAllByMemberId(currentLecturer.getId());
        List<List<EnrollmentInfo>> lecturerEnrollmentInfos = lectures.stream()
            .map(lecture ->
                enrollmentInfoRepository.findAllByLectureAndEnrollmentState(lecture, EnrollmentState.APPROVAL))
            .collect(Collectors.toList());
        return LectureResponse.createLectureResponseFromEnrollmentInfos(lecturerEnrollmentInfos);
    }

    @Transactional
    // TODO : 이름 바꾸기
    public int openLectureAndGetAttendanceNumber(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
            .orElseThrow(() -> LectureNotFoundException.EXCEPTION);
        authenticationHelper.verifyRequestMemberLogInMember(lecture.getMember().getId());

        lecture.setLectureState(LectureState.OPEN);

        int attendanceNumber = generateRandomNumber();
        attendanceService.saveAttendanceNumber(lecture.getId(), attendanceNumber);

        log.info("[강의 OPEN] {} ({}), 출석 번호 : " + attendanceNumber
            , () -> lecture.getLectureName(), () -> lecture.getId());
        return attendanceNumber;
    }

    @Transactional
    public void closeLecture(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
            .orElseThrow(() -> LectureNotFoundException.EXCEPTION);
        authenticationHelper.verifyRequestMemberLogInMember(lecture.getMember().getId());

        lecture.setLectureState(LectureState.CLOSED);

        log.info("[강의 CLOSE] {} ({})"
            , () -> lecture.getLectureName(), () -> lecture.getId());
    }

    private Integer generateRandomNumber() {
        int rangeSize = RANDOM_NUMBER_END - RANDOM_NUMBER_START + 1;
        return (int) (Math.random() * (rangeSize)) + RANDOM_NUMBER_START;
    }
}
