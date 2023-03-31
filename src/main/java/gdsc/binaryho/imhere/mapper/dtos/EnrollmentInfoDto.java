package gdsc.binaryho.imhere.mapper.dtos;

import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Tag(name = "EnrollmentInfoDto", description = "학생의 수강신청 정보")
public class EnrollmentInfoDto {

    private Long lectureId;
    private String lectureName;
    private String lecturerName;

    @Schema(description = "학생 정보와 수강신청 승인 상태 리스트")
    List<StudentInfo> studentInfos;

    public EnrollmentInfoDto() {
    }

    public static EnrollmentInfoDto createEnrollmentInfoDto(List<EnrollmentInfo> enrollmentInfos) {

        if (enrollmentInfos.isEmpty()) {
            return new EnrollmentInfoDto();
        }

        EnrollmentInfoDto enrollmentInfoDto = new EnrollmentInfoDto();
        EnrollmentInfo anyEnrollmentInfo = enrollmentInfos.get(0);
        enrollmentInfoDto.lectureId = anyEnrollmentInfo.getLecture().getId();
        enrollmentInfoDto.lectureName = anyEnrollmentInfo.getLecture().getLectureName();
        enrollmentInfoDto.lecturerName = anyEnrollmentInfo.getLecture().getLecturerName();
        enrollmentInfoDto.studentInfos =
            enrollmentInfos.stream()
                .map(StudentInfo::createStudentInfo)
                .collect(Collectors.toList());

        return enrollmentInfoDto;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @Tag(name = "StudentInfo", description = "학생 정보와 수강신청 승인 상태")
    public static class StudentInfo {

        private Long id;
        private String univId;
        private String name;
        @Schema(description = "학생 수강신청 승인 상태")
        private EnrollmentState enrollmentState;

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
