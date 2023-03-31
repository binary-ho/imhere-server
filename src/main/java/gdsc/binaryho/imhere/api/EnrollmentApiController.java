package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.mapper.dtos.EnrollmentInfoDto;
import gdsc.binaryho.imhere.mapper.requests.EnrollMentRequestForLecturer;
import gdsc.binaryho.imhere.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Enrollment", description = "수강 신청 관련 API입니다.")
@RestController
public class EnrollmentApiController {

    private final EnrollmentService enrollmentService;

    public EnrollmentApiController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @Operation(summary = "강사가 학생 리스트를 통해 학생을 등록하는 API")
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

    // TODO : 예외 처리 필요
    @Operation(summary = "특정 수업의 출석 정보를 가져오는 API")
    @GetMapping("/api/v1/enrollment/{lecture_id}")
    public EnrollmentInfoDto getLectureEnrollment(@PathVariable("lecture_id") Long lectureId) {
        return enrollmentService.getLectureEnrollment(lectureId);
    }

    @Operation(summary = "강사가 수업에 수강신청한 학생을 승인하는 API")
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

    @Operation(summary = "강사가 수업에 수강신청한 학생을 반려하는 API")
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

    @Operation(summary = "학생이 수업에 수강신청을 하는 API")
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
