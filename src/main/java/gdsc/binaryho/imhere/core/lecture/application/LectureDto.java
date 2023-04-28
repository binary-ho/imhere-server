package gdsc.binaryho.imhere.core.lecture.application;

import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.lecture.Lecture;
import gdsc.binaryho.imhere.core.lecture.LectureState;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Tag(name = "LectureDto", description = "수업 정보")
public class LectureDto {

    private Long lectureId;
    private String lectureName;
    private String lecturerName;
    private LectureState lectureState;
    @Schema(description = "강사의 경우 본인 수업 학생 리스트 받아볼 수 있음")
    private List<StudentInfo> studentInfos;

    public LectureDto() {}

    public static LectureDto createLectureDtoWithEnrollmentInfo(Lecture lecture, List<EnrollmentInfo> enrollmentInfos) {
        LectureDto lectureDto = createLectureDto(lecture);
        lectureDto.studentInfos = enrollmentInfos.stream()
            .map(LectureDto::createStudentInfo)
            .collect(Collectors.toList());

        return lectureDto;
    }

    private static StudentInfo createStudentInfo(EnrollmentInfo enrollmentInfo) {
        return new StudentInfo(enrollmentInfo.getMember().getId(),
            enrollmentInfo.getMember().getUnivId(), enrollmentInfo.getMember().getName());
    }

    public static LectureDto createLectureDto(Lecture lecture) {
        LectureDto lectureDto = new LectureDto();
        lectureDto.lectureId = lecture.getId();
        lectureDto.lectureName = lecture.getLectureName();
        lectureDto.lecturerName = lecture.getLecturerName();
        lectureDto.lectureState = lecture.getLectureState();
        lectureDto.studentInfos = new ArrayList<>();
        return lectureDto;
    }

    @Getter @Setter
    @NoArgsConstructor
    @Tag(name = "StudentInfo", description = "학생 정보")
    public static class StudentInfo {

        private Long id;
        private String univId;
        private String name;

        public StudentInfo(Long id, String univId, String name) {
            this.id = id;
            this.univId = univId;
            this.name = name;
        }
    }
}
