package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.domain.enrollment.EnrollRequest;
import gdsc.binaryho.imhere.service.EnrollmentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnrollmentApiController {

    private final EnrollmentService enrollmentService;

    public EnrollmentApiController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/api/v1/lecturer/enrollment")
    public void enrollLecture(@RequestBody EnrollRequest enrollRequest) {
        /* TODO: security 도입 이후 context 에서 id 가져와서 비교 해야함 */
        enrollmentService.enrollStudents(enrollRequest);
    }
}
