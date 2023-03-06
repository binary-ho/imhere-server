package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureCreateRequest;
import gdsc.binaryho.imhere.domain.lecture.LectureRepository;
import gdsc.binaryho.imhere.domain.lecture.LectureState;
import gdsc.binaryho.imhere.domain.roster.LectureStudent;
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

    @GetMapping("/api/v1/lecturer/{id}/lectures")
    public List<LectureDto> getLectures(@PathVariable("id") Long id) {
        List<Lecture> lectures = lectureRepository.findAllByMemberId(id);
        return lectures.stream().map(LectureDto::createLectureDtoForLecturer).collect(Collectors.toList());
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
        private List<LectureStudent> lectureStudents;

        private LectureDto() {}

        public static LectureDto createLectureDtoForLecturer(Lecture lecture) {
            LectureDto lectureDto = createLectureDto(lecture);
            lectureDto.lectureStudents = List.copyOf(lecture.getLectureStudents());
            return lectureDto;
        }

        public static LectureDto createLectureDto(Lecture lecture) {
            LectureDto lectureDto = new LectureDto();
            lectureDto.id = lecture.getId();
            lectureDto.lectureName = lecture.getLectureName();
            lectureDto.lecturerName = lecture.getLecturerName();
            lectureDto.lectureState = lecture.getLectureState();
            lectureDto.lectureStudents = new ArrayList<>();
            return lectureDto;
        }
    }
}
