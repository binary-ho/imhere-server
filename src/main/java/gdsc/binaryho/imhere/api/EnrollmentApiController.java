package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.mapper.requests.EnrollRequest;
import gdsc.binaryho.imhere.service.EnrollmentService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public HttpEntity<String> enrollStudents(@RequestBody EnrollRequest enrollRequest, @PathVariable("lecture_id") Long lecture_id) {

        try {
            System.out.println("enrollRequest = " + enrollRequest.getUnivIds().toString());
            enrollmentService.enrollStudents(enrollRequest, lecture_id);
            return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
