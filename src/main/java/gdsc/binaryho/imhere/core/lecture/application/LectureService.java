package gdsc.binaryho.imhere.core.lecture.application;

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
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class LectureService {

    private final static Integer RANDOM_NUMBER_START = 100;
    private final static Integer RANDOM_NUMBER_END = 1000;
    private final static Integer ATTENDANCE_NUMBER_EXPIRE_TIME = 10;

    private final AuthenticationHelper authenticationHelper;
    private final LectureRepository lectureRepository;
    private final EnrollmentInfoRepository enrollmentInfoRepository;
    private final RedisTemplate<String, String> redisTemplate;

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
    public int openLectureAndGetAttendanceNumber(Long lectureId) {
        Optional<Lecture> findLecture = lectureRepository.findById(lectureId);

        validateLectureNonNull(findLecture);

        Lecture lecture = findLecture.get();
        authenticationHelper.verifyRequestMemberLogInMember(lecture.getMember().getId());

        lecture.setLectureState(LectureState.OPEN);

        Integer attendanceNumber = generateRandomNumber();

        redisTemplate.opsForValue().set(
            String.valueOf(lecture.getId()),
            String.valueOf(attendanceNumber),
            ATTENDANCE_NUMBER_EXPIRE_TIME,
            TimeUnit.MINUTES
        );

        log.info("[강의 OPEN] {} ({}), 출석 번호 : " + attendanceNumber
            , () -> lecture.getLectureName(), () -> lecture.getId());
        return attendanceNumber;
    }

    @Transactional
    public void closeLecture(Long lectureId) {
        Optional<Lecture> findLecture = lectureRepository.findById(lectureId);

        validateLectureNonNull(findLecture);

        Lecture lecture = findLecture.get();
        authenticationHelper.verifyRequestMemberLogInMember(lecture.getMember().getId());

        lecture.setLectureState(LectureState.CLOSED);

        log.info("[강의 CLOSE] {} ({})"
            , () -> lecture.getLectureName(), () -> lecture.getId());
    }

    private Integer generateRandomNumber() {
        int rangeSize = RANDOM_NUMBER_END - RANDOM_NUMBER_START + 1;
        return (int) (Math.random() * (rangeSize)) + RANDOM_NUMBER_START;
    }

    private void validateLectureNonNull(Optional<Lecture> findLecture) {
        if (findLecture.isEmpty()) {
            throw LectureNotFoundException.EXCEPTION;
        }
    }
}
