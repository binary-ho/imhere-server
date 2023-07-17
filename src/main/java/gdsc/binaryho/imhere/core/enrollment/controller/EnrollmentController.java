package gdsc.binaryho.imhere.core.enrollment.controller;

import gdsc.binaryho.imhere.core.enrollment.model.response.EnrollmentInfoResponse;
import gdsc.binaryho.imhere.core.enrollment.application.EnrollmentService;
import gdsc.binaryho.imhere.exception.ImhereException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Tag(name = "Enrollment", description = "수강 신청 관련 API입니다.")
@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @Operation(summary = "특정 수업의 수강 신청 정보를 가져오는 API")
    @GetMapping("/{lecture_id}")
    public ResponseEntity<EnrollmentInfoResponse> getLectureEnrollment(@PathVariable("lecture_id") Long lectureId) {
        return ResponseEntity.ok(enrollmentService.getLectureEnrollment(lectureId));
    }

    @Operation(summary = "강사가 수업에 수강신청한 학생을 승인하는 API")
    @PostMapping("/{lecture_id}/student/{student_id}/approval")
    public ResponseEntity<Void> approveStudents(@PathVariable("lecture_id") Long lectureId, @PathVariable("student_id") Long studentId) {

        try {
            enrollmentService.approveStudents(lectureId, studentId);
            return ResponseEntity.ok().build();
        } catch (ImhereException error) {
            log.info("[수강신청 승인 ERROR] : " + error);
            return ResponseEntity
                .status(error.getErrorInfo().getCode())
                .build();
        }
    }

    @Operation(summary = "강사가 수업에 수강신청한 학생을 반려하는 API")
    @PostMapping("/{lecture_id}/student/{student_id}/rejection")
    public ResponseEntity<Void> rejectStudents(@PathVariable("lecture_id") Long lectureId, @PathVariable("student_id") Long studentId) {

        try {
            enrollmentService.rejectStudents(lectureId, studentId);
            return ResponseEntity.ok().build();
        } catch (ImhereException error) {
            return ResponseEntity
                .status(error.getErrorInfo().getCode())
                .build();
        }
    }

    @Operation(summary = "학생이 수업에 수강신청을 하는 API")
    @PostMapping("/{lecture_id}")
    public ResponseEntity<Void> requestEnrollment(@PathVariable("lecture_id") Long lectureId) {

        try {
            enrollmentService.requestEnrollment(lectureId);
            return ResponseEntity.ok().build();
        } catch (ImhereException error) {
            return ResponseEntity
                .status(error.getErrorInfo().getCode())
                .build();
        }
    }
}
