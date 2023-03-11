package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureRepository;
import gdsc.binaryho.imhere.domain.lecture.LectureState;
import gdsc.binaryho.imhere.mapper.dtos.LectureDto;
import gdsc.binaryho.imhere.mapper.requests.LectureCreateRequest;
import gdsc.binaryho.imhere.service.LectureService;
import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LectureApiController {

    private final LectureService lectureService;
    private final LectureRepository lectureRepository;

    @GetMapping("/api/v1/students/all-lectures")
    public List<LectureDto> getAllLectures() {
        List<Lecture> lectures = lectureRepository.findAllByLectureStateNot(LectureState.TERMINATED);
        return lectures.stream().map(LectureDto::createLectureDto).collect(Collectors.toList());
    }

    @GetMapping("/api/v1/students/lectures")
    public List<LectureDto> getStudentLectures() throws NoSuchObjectException {
        List<Lecture> lectures = lectureService.getStudentLectures();
        return lectures.stream().map(LectureDto::createLectureDto).collect(Collectors.toList());
    }

    @GetMapping("/api/v1/students/open-lectures")
    public List<LectureDto> getStudentOpenLectures() throws NoSuchObjectException {
        List<Lecture> lectures = lectureService.getStudentOpenLectures();
        return lectures.stream().map(LectureDto::createLectureDto).collect(Collectors.toList());
    }

    /*
    * 강사 본인이 만든 강의들 가져오기
    * */
    @GetMapping("/api/v1/lectures")
    public List<LectureDto> getLectures() throws NoSuchObjectException {
        return lectureService.getOwnLectures();
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

    /*
     * 강의 닫기
     * */
    @PostMapping("/api/v1/lectures/{lecture_id}/close")
    public ResponseEntity<String> changeLectureState(@PathVariable("lecture_id") Long lecture_id) {
        try {
            lectureService.closeLecture(lecture_id);
            return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
