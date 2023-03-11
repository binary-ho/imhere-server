package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureRepository;
import gdsc.binaryho.imhere.domain.lecture.LectureState;
import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.mapper.dtos.LectureDto;
import gdsc.binaryho.imhere.mapper.requests.LectureCreateRequest;
import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final static Integer RANDOM_NUMBER_START = 100;
    private final static Integer RANDOM_NUMBER_END = 1000;
    private final static Integer ATTENDANCE_NUMBER_EXPIRE_TIME = 10;

    private final LectureRepository lectureRepository;
    private final EnrollmentInfoRepository enrollmentInfoRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void createLecture(LectureCreateRequest request) throws NoSuchObjectException {
        Member lecturer = AuthenticationService.getCurrentMember();
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

    public List<LectureDto> getOwnLectures() throws NoSuchObjectException {
        Member currentLecturer = AuthenticationService.getCurrentMember();
        List<Lecture> lectures = lectureRepository.findAllByMemberId(currentLecturer.getId());
        return lectures.stream().map(lecture ->
            LectureDto.createLectureDtoWithEnrollmentInfo(lecture,
                enrollmentInfoRepository.findAllByLecture(lecture))
        ).collect(Collectors.toList());
    }

    @Transactional
    public int openLectureAndGetAttendanceNumber(Long lectureId) throws Exception {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        AuthenticationService.verifyRequestMemberLogInMember(lecture.getMember().getId());

        lecture.setLectureState(LectureState.OPEN);

        Integer attendanceNumber = generateRandomNumber();

        redisTemplate.opsForValue().set(
            String.valueOf(lecture.getId()),
            String.valueOf(attendanceNumber),
            ATTENDANCE_NUMBER_EXPIRE_TIME,
            TimeUnit.MINUTES
        );

        return attendanceNumber;
    }

    @Transactional
    public void closeLecture(Long lectureId) throws Exception {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        AuthenticationService.verifyRequestMemberLogInMember(lecture.getMember().getId());

        lecture.setLectureState(LectureState.CLOSED);
    }

    private Integer generateRandomNumber() {
        int rangeSize = RANDOM_NUMBER_END - RANDOM_NUMBER_START + 1;
        return (int) (Math.random() * (rangeSize)) + RANDOM_NUMBER_START;
    }
}
