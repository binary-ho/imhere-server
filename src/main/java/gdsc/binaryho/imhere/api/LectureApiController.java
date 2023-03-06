package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureRepository;
import gdsc.binaryho.imhere.service.LectureService;
import java.net.http.HttpResponse;
import java.util.List;
import javax.validation.Valid;
import lombok.Data;
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
    public List<Lecture> getLectures(@PathVariable("id") Long id) {
        return lectureRepository.findAllByMemberId(id);
    }

    @PostMapping("/api/v1/lecture/new")
    public ResponseEntity<String> createLecture(@RequestBody LectureCreateRequest request) {
        try {
            lectureService.createLecture(request.getLecturerId(), request.getClassName());
            return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Getter
    private static class LectureCreateRequest {
        private final Long lecturerId;
        private final String className;

        public LectureCreateRequest(Long lecturerId, String className) {
            this.lecturerId = lecturerId;
            this.className = className;
        }
    }
}
