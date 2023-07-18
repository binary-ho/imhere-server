package gdsc.binaryho.imhere.core.enrollment.model.response;

import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.core.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
@Tag(name = "EnrollmentInfoDto", description = "학생의 수강신청 정보")
public class EnrollmentInfoResponse {

    private Long lectureId;
    private String lectureName;
    private String lecturerName;

    @Schema(description = "학생 정보와 수강신청 승인 상태 리스트")
    List<StudentInfo> studentInfos;

    private EnrollmentInfoResponse() {
    }

    public static EnrollmentInfoResponse createEnrollmentInfoDto(List<EnrollmentInfo> enrollmentInfos) {

        if (enrollmentInfos.isEmpty()) {
            return new EnrollmentInfoResponse();
        }

        EnrollmentInfoResponse enrollmentInfoResponse = new EnrollmentInfoResponse();
        EnrollmentInfo anyEnrollmentInfo = enrollmentInfos.get(0);
        enrollmentInfoResponse.lectureId = anyEnrollmentInfo.getLecture().getId();
        enrollmentInfoResponse.lectureName = anyEnrollmentInfo.getLecture().getLectureName();
        enrollmentInfoResponse.lecturerName = anyEnrollmentInfo.getLecture().getLecturerName();
        enrollmentInfoResponse.studentInfos =
            enrollmentInfos.stream()
                .map(StudentInfo::createStudentInfo)
                .collect(Collectors.toList());

        return enrollmentInfoResponse;
    }


    @Getter
    @Tag(name = "StudentInfo", description = "학생 정보와 수강신청 승인 상태")
    public static class StudentInfo {

        private final Long id;
        private final String univId;
        private final String name;
        @Schema(description = "학생 수강신청 승인 상태")
        private final EnrollmentState enrollmentState;

        private StudentInfo(Long id, String univId, String name,
            EnrollmentState enrollmentState) {
            this.id = id;
            this.univId = univId;
            this.name = name;
            this.enrollmentState = enrollmentState;
        }

        public static StudentInfo createStudentInfo(EnrollmentInfo enrollmentInfo) {
            Member member = enrollmentInfo.getMember();
            return new StudentInfo(member.getId(), member.getUnivId(), member.getName(),
                enrollmentInfo.getEnrollmentState());
        }
    }
}
