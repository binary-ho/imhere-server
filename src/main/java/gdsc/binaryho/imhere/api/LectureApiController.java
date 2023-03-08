package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureCreateRequest;
import gdsc.binaryho.imhere.domain.lecture.LectureRepository;
import gdsc.binaryho.imhere.domain.lecture.LectureState;
import gdsc.binaryho.imhere.domain.lecture.LectureStateChangeRequest;
import gdsc.binaryho.imhere.service.LectureService;
import java.util.ArrayList;
import java.util.List;
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

    private final LectureRepository lectureRepository;
    private final LectureService lectureService;

    /* TODO: 시큐리티 도입 이후 제거 */
    private final static Long mockLoginUserId = 1L;

    public LectureApiController(LectureRepository lectureRepository, LectureService lectureService) {
        this.lectureRepository = lectureRepository;
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
    public List<LectureDto> getLectures() {
        /* TODO : 시큐리티 도입 이후 id 가져와야함 */
        List<Lecture> lectures = lectureRepository.findAllByMemberId(mockLoginUserId);
        return lectures.stream().map(LectureDto::createLectureDtoWithLectureStudents).collect(Collectors.toList());
    }

    /*
     * 강의 생성
     * */
    @PostMapping("/api/v1/lectures")
    public ResponseEntity<String> createLecture(@RequestBody LectureCreateRequest request) {
        try {
            /* TODO : 시큐리티 도입 이후 id 가져와야함 */
            lectureService.createLecture(request, mockLoginUserId);
            return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /*
    * 강의 상태 변경
    * */
    @PostMapping("/api/v1/lectures/{lecture_id}/state")
    public ResponseEntity<String> changeLectureState(@RequestBody LectureStateChangeRequest lectureStateChangeRequest,
        @PathVariable("lecture_id") Long lecture_id) {
        /* TODO: 시큐리티 도입 이후, 강의 소유자 검증 로직 필요 */
        try {
            lectureService.changeLectureState(lectureStateChangeRequest, lecture_id, mockLoginUserId);
            return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
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
