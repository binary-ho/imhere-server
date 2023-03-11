package gdsc.binaryho.imhere.mapper.dtos;

import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.domain.member.Member;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class EnrollmentInfoDto {

    private String lectureName;
    private String lecturerName;
    List<StudentInfo> studentInfos;

    public EnrollmentInfoDto() {
    }

    public static EnrollmentInfoDto createEnrollmentInfoDto(List<EnrollmentInfo> enrollmentInfos) {

        if (enrollmentInfos.isEmpty()) {
            return new EnrollmentInfoDto();
        }

        EnrollmentInfoDto enrollmentInfoDto = new EnrollmentInfoDto();
        EnrollmentInfo anyEnrollmentInfo = enrollmentInfos.get(0);
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
    public static class StudentInfo {

        private Long id;
        private String univId;
        private String name;
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
