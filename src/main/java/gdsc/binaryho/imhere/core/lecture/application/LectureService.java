package gdsc.binaryho.imhere.core.lecture.application;

import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.core.enrollment.infrastructure.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.core.lecture.LectureState;
import gdsc.binaryho.imhere.core.lecture.application.port.AttendeeCacheRepository;
import gdsc.binaryho.imhere.core.lecture.application.port.OpenLectureCacheRepository;
import gdsc.binaryho.imhere.core.lecture.domain.AttendeeCacheEvent;
import gdsc.binaryho.imhere.core.lecture.domain.Lecture;
import gdsc.binaryho.imhere.core.lecture.domain.OpenLecture;
import gdsc.binaryho.imhere.core.lecture.domain.OpenLectures;
import gdsc.binaryho.imhere.core.lecture.exception.LectureNotFoundException;
import gdsc.binaryho.imhere.core.lecture.infrastructure.LectureRepository;
import gdsc.binaryho.imhere.core.lecture.model.LectureInfo;
import gdsc.binaryho.imhere.core.lecture.model.StudentIds;
import gdsc.binaryho.imhere.core.lecture.model.request.LectureCreateRequest;
import gdsc.binaryho.imhere.core.lecture.model.response.AttendanceNumberResponse;
import gdsc.binaryho.imhere.core.lecture.model.response.LectureResponse;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.security.util.AuthenticationHelper;
import gdsc.binaryho.imhere.util.SeoulDateTimeHolder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class LectureService {

    private static final Integer RANDOM_NUMBER_START = 100;
    private static final Integer RANDOM_NUMBER_END = 1000;

    private final AuthenticationHelper authenticationHelper;
    private final LectureRepository lectureRepository;
    private final EnrollmentInfoRepository enrollmentInfoRepository;
    private final OpenLectureCacheRepository openLectureCacheRepository;
    private final AttendeeCacheRepository attendeeCacheRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final SeoulDateTimeHolder seoulDateTimeHolder;

    @Transactional
    public void createLecture(LectureCreateRequest request) {
        Member lecturer = authenticationHelper.getCurrentMember();
        Lecture newLecture = Lecture
            .createLecture(lecturer, request.getLectureName(), seoulDateTimeHolder.getSeoulDateTime());
        lectureRepository.save(newLecture);
    }

    @Transactional(readOnly = true)
    public LectureResponse getStudentLectures() {
        Member currentStudent = authenticationHelper.getCurrentMember();
        List<Lecture> studentLectures = findStudentLectures(currentStudent);
        return LectureResponse.from(studentLectures);
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

        OpenLectures openLectures = findCachedOpenLectures(currentStudent.getId());

        if (openLectures.isNotEmpty()) {
            return LectureResponse.from(openLectures);
        }

        List<Lecture> studentOpenLectures = lectureRepository.findOpenAndApprovalLecturesByMemberId(currentStudent.getId());
        return LectureResponse.from(studentOpenLectures);
    }

    private OpenLectures findCachedOpenLectures(Long studentId) {
        Set<Long> lectureIds = attendeeCacheRepository.findAllAttendLectureId(studentId);

        List<OpenLecture> openLectures = lectureIds.stream()
            .map(openLectureCacheRepository::find)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

        return new OpenLectures(openLectures);
    }

    @Transactional(readOnly = true)
    public LectureResponse getOwnedLectures() {
        Member currentLecturer = authenticationHelper.getCurrentMember();
        List<Lecture> lectures = lectureRepository.findAllByMemberId(currentLecturer.getId());
        List<LectureInfo> lectureInfos = lectures.stream()
            .map(this::createLectureInfo)
            .collect(Collectors.toList());

        return new LectureResponse(lectureInfos);
    }

    private LectureInfo createLectureInfo(Lecture lecture) {
        List<EnrollmentInfo> enrollmentInfos = enrollmentInfoRepository
            .findAllApprovedByLectureId(lecture.getId());

        return LectureInfo.from(lecture, enrollmentInfos);
    }

    @Transactional
    public AttendanceNumberResponse openLectureAndGenerateAttendanceNumber(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
            .orElseThrow(() -> LectureNotFoundException.EXCEPTION);
        authenticationHelper.verifyRequestMemberLogInMember(lecture.getMember().getId());

        lecture.setLectureState(LectureState.OPEN);
        lecture.setLastOpeningTime(seoulDateTimeHolder.getSeoulDateTime());

        int attendanceNumber = generateRandomNumber();
        saveOpenLecture(lecture, attendanceNumber);
        cacheAttendee(lecture);

        log.info("[강의 OPEN] {} ({}), 출석 번호 : " + attendanceNumber
            , lecture::getLectureName, lecture::getId);

        return new AttendanceNumberResponse(attendanceNumber);
    }

    private void saveOpenLecture(Lecture lecture, int attendanceNumber) {
        OpenLecture openLecture = OpenLecture.from(lecture, attendanceNumber);
        openLectureCacheRepository.cache(openLecture);
    }

    private void cacheAttendee(Lecture lecture) {
        List<Long> studentIds = enrollmentInfoRepository.findAllApprovedByLectureId(lecture.getId())
            .stream()
            .map(EnrollmentInfo::getMember)
            .map(Member::getId)
            .collect(Collectors.toList());

        eventPublisher.publishEvent(
            new AttendeeCacheEvent(lecture.getId(), new StudentIds(studentIds)));
    }

    @Transactional
    public void closeLecture(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
            .orElseThrow(() -> LectureNotFoundException.EXCEPTION);
        authenticationHelper.verifyRequestMemberLogInMember(lecture.getMember().getId());

        lecture.setLectureState(LectureState.CLOSED);

        log.info("[강의 CLOSE] {} ({})"
            , lecture::getLectureName, lecture::getId);
    }

    private Integer generateRandomNumber() {
        int rangeSize = RANDOM_NUMBER_END - RANDOM_NUMBER_START + 1;
        return (int) (Math.random() * (rangeSize)) + RANDOM_NUMBER_START;
    }
}
