package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureRepository;
import gdsc.binaryho.imhere.domain.lecture.LectureState;
import java.util.List;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LectureApiController {

    private final LectureRepository lectureRepository;

    public LectureApiController(
        LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    @GetMapping("/api/v1/lecturer/{id}/lectures")
    public List<Lecture> getLectures(@PathVariable("id") Long id) {
        return lectureRepository.findAllByMemberId(id);
    }
}
