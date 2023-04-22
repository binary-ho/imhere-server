package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureRepository;
import gdsc.binaryho.imhere.domain.lecture.LectureState;
import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.mapper.dtos.LectureDto;
import gdsc.binaryho.imhere.mapper.requests.LectureCreateRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public List<Lecture> getStudentLectures() {
        Member currentStudent = authenticationHelper.getCurrentMember();
        List<EnrollmentInfo> enrollmentInfos = enrollmentInfoRepository
            .findAllByMemberIdAndEnrollmentState(currentStudent.getId(), EnrollmentState.APPROVAL);

        return getLectures(enrollmentInfos);
    }

    public List<Lecture> getStudentOpenLectures() {
        Member currentStudent = authenticationHelper.getCurrentMember();
        List<EnrollmentInfo> enrollmentInfos = enrollmentInfoRepository
            .findAllByMemberIdAndLecture_LectureStateAndEnrollmentState(currentStudent.getId(), LectureState.OPEN, EnrollmentState.APPROVAL);

        return getLectures(enrollmentInfos);
    }

    private List<Lecture> getLectures(List<EnrollmentInfo> enrollmentInfos) {
        return enrollmentInfos.stream()
            .map(EnrollmentInfo::getLecture)
            .collect(Collectors.toList());
    }

    public List<LectureDto> getOwnedLectures() {
        Member currentLecturer = authenticationHelper.getCurrentMember();
        List<Lecture> lectures = lectureRepository.findAllByMemberId(currentLecturer.getId());
        return lectures.stream().map(lecture ->
            LectureDto.createLectureDtoWithEnrollmentInfo(lecture,
                enrollmentInfoRepository.findAllByLectureAndEnrollmentState(lecture, EnrollmentState.APPROVAL))
        ).collect(Collectors.toList());
    }

    @Transactional
    public int openLectureAndGetAttendanceNumber(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        authenticationHelper.verifyRequestMemberLogInMember(lecture.getMember().getId());

        lecture.setLectureState(LectureState.OPEN);

        Integer attendanceNumber = generateRandomNumber();

        redisTemplate.opsForValue().set(
            String.valueOf(lecture.getId()),
            String.valueOf(attendanceNumber),
            ATTENDANCE_NUMBER_EXPIRE_TIME,
            TimeUnit.MINUTES
        );

        log.info("[강의 OPEN] " + lecture.getLectureName() + " (" + lecture.getId()
            + ") 출석 번호 : " + attendanceNumber);
        return attendanceNumber;
    }

    @Transactional
    public void closeLecture(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        authenticationHelper.verifyRequestMemberLogInMember(lecture.getMember().getId());

        lecture.setLectureState(LectureState.CLOSED);

        log.info("[강의 CLOSE] " + lecture.getLectureName() + " (" + lecture.getId() + ")");
    }

    private Integer generateRandomNumber() {
        int rangeSize = RANDOM_NUMBER_END - RANDOM_NUMBER_START + 1;
        return (int) (Math.random() * (rangeSize)) + RANDOM_NUMBER_START;
    }
}
