package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.mapper.dtos.EnrollmentInfoDto;
import gdsc.binaryho.imhere.mapper.requests.EnrollMentRequestForLecturer;
import gdsc.binaryho.imhere.service.EnrollmentService;
import java.rmi.NoSuchObjectException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnrollmentApiController {

    private final EnrollmentService enrollmentService;

    public EnrollmentApiController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/api/v1/enrollment/{lecture_id}")
    public HttpEntity<String> enrollStudentsForLecturer(@RequestBody EnrollMentRequestForLecturer enrollMentRequestForLecturer, @PathVariable("lecture_id") Long lectureId) {

        try {
            enrollmentService.enrollStudents(enrollMentRequestForLecturer, lectureId);
            return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/api/v1/enrollment/{lecture_id}")
    public EnrollmentInfoDto getLectureEnrollment(@PathVariable("lecture_id") Long lectureId)
        throws NoSuchObjectException {
        return enrollmentService.getLectureEnrollment(lectureId);
    }

    @PostMapping("/api/v1/enrollment/{lecture_id}/student/{student_id}/approval")
    public HttpEntity<String> approveStudents(@PathVariable("lecture_id") Long lectureId, @PathVariable("student_id") Long studentId) {

        try {
            enrollmentService.approveStudents(lectureId, studentId);
            return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/api/v1/enrollment/{lecture_id}/student/{student_id}/rejection")
    public HttpEntity<String> rejectStudents(@PathVariable("lecture_id") Long lectureId, @PathVariable("student_id") Long studentId) {

        try {
            enrollmentService.rejectStudents(lectureId, studentId);
            return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/api/v1/students/enrollment/{lecture_id}")
    public HttpEntity<String> requestEnrollment(@PathVariable("lecture_id") Long lectureId) {

        try {
            enrollmentService.requestEnrollment(lectureId);
            return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
