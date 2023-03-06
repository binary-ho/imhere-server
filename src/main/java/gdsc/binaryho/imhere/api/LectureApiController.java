package gdsc.binaryho.imhere.api;

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

@RestController
public class LectureApiController {

    private final LectureRepository lectureRepository;
    private final LectureService lectureService;

    public LectureApiController(LectureRepository lectureRepository, LectureService lectureService) {
        this.lectureRepository = lectureRepository;
        this.lectureService = lectureService;
    }

    @GetMapping("/api/v1/member/{id}/lectures")
    public List<LectureDto> getStudentLectures(@PathVariable("id") Long student_id) {
        List<Lecture> lectures = lectureService.getStudentLectures(student_id);
        return lectures.stream().map(LectureDto::createLectureDto).collect(Collectors.toList());
    }

    @GetMapping("/api/v1/member/{id}/open-lectures")
    public List<LectureDto> getStudentOpenLectures(@PathVariable("id") Long student_id) {
        List<Lecture> lectures = lectureService.getStudentOpenLectures(student_id);
        return lectures.stream().map(LectureDto::createLectureDto).collect(Collectors.toList());
    }

    @GetMapping("/api/v1/lecturer/{id}/lectures")
    public List<LectureDto> getLectures(@PathVariable("id") Long lecturer_id) {
        List<Lecture> lectures = lectureRepository.findAllByMemberId(lecturer_id);
        return lectures.stream().map(LectureDto::createLectureDtoWithLectureStudents).collect(Collectors.toList());
    }

    @PostMapping("/api/v1/lecturer/state")
    public ResponseEntity<String> changeLectureState(@RequestBody LectureStateChangeRequest lectureStateChangeRequest) {
        /* TODO: 강의 소유자 검증 로직 필요 */
        try {
            lectureService.changeLectureState(lectureStateChangeRequest);
            return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/api/v1/lecture/new")
    public ResponseEntity<String> createLecture(@RequestBody LectureCreateRequest request) {
        try {
            lectureService.createLecture(request);
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
        private List<gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo> enrollmentInfos;

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
