package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureRepository;
import gdsc.binaryho.imhere.domain.lecture.LectureState;
import gdsc.binaryho.imhere.mapper.requests.LectureCreateRequest;
import gdsc.binaryho.imhere.service.LectureService;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/* TODO: 유저의 권한을 바꾸는 API 필요
*  */
@RestController
public class LectureApiController {

    private final LectureService lectureService;
    private static Long mockLoginUserId = 1L;

    public LectureApiController(LectureRepository lectureRepository, LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @GetMapping("/api/v1/students/{student_id}/lectures")
    public List<LectureDto> getStudentLectures(@PathVariable("student_id") Long student_id) {
        List<Lecture> lectures = lectureService.getStudentLectures(student_id);
        return lectures.stream().map(LectureDto::createLectureDto).collect(Collectors.toList());
    }

    @GetMapping("/api/v1/students/{student_id}/open-lectures")
    public List<LectureDto> getStudentOpenLectures(@PathVariable("student_id") Long student_id) {
        List<Lecture> lectures = lectureService.getStudentOpenLectures(student_id);
        return lectures.stream().map(LectureDto::createLectureDto).collect(Collectors.toList());
    }

    /*
    * 강사 본인이 만든 강의들 가져오기
    * */
    @GetMapping("/api/v1/lectures")
    public List<LectureDto> getLectures() throws NoSuchObjectException {
        /* TODO : 시큐리티 도입 이후 id 가져와야함 */
        List<Lecture> lectures = lectureService.getOwnLectures();
        return lectures.stream().map(LectureDto::createLectureDtoWithLectureStudents).collect(Collectors.toList());
    }

    /*
     * 강의 생성
     * */
    @PostMapping("/api/v1/lectures")
    public ResponseEntity<String> createLecture(@RequestBody LectureCreateRequest request) {
        try {
            lectureService.createLecture(request);
            return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

//    /*
//    * 강의 상태 변경
//    * */
//    @PostMapping("/api/v1/lectures/{lecture_id}/state")
//    public ResponseEntity<String> changeLectureState(@RequestBody LectureStateChangeRequest lectureStateChangeRequest,
//        @PathVariable("lecture_id") Long lecture_id) {
//        try {
//            lectureService.changeLectureState(lectureStateChangeRequest, lecture_id, mockLoginUserId);
//            return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//    }

    /*
     * 강의 열고 출석 번호 반환
     * */
    @PostMapping("/api/v1/lectures/{lecture_id}/open")
    public ResponseEntity<String> openLectureAndGetAttendanceNumber(@PathVariable("lecture_id") Long lecture_id) {
        try {
            int attendanceNumber = lectureService.openLectureAndGetAttendanceNumber(lecture_id);
            return ResponseEntity.ok(Map.of("attendanceNumber", attendanceNumber).toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Getter
    private static class LectureDto {
        private Long id;
        private String lectureName;
        private String lecturerName;
        private LectureState lectureState;
        private List<EnrollmentInfo> enrollmentInfos;

        private LectureDto() {}

        public static LectureDto createLectureDtoWithLectureStudents(Lecture lecture) {
            LectureDto lectureDto = createLectureDto(lecture);
            lectureDto.enrollmentInfos = List.copyOf(lecture.getEnrollmentInfos());
            return lectureDto;
        }

        public static LectureDto createLectureDto(Lecture lecture) {
            LectureDto lectureDto = new LectureDto();
            lectureDto.id = lecture.getId();
            lectureDto.lectureName = lecture.getLectureName();
            lectureDto.lecturerName = lecture.getLecturerName();
            lectureDto.lectureState = lecture.getLectureState();
            lectureDto.enrollmentInfos = new ArrayList<>();
            return lectureDto;
        }
    }
}
