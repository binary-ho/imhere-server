package gdsc.binaryho.imhere.core.lecture.model.response;

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
public class LectureResponse {

    private Long lectureId;
    private String lectureName;
    private String lecturerName;
    private LectureState lectureState;
    @Schema(description = "강사의 경우 본인 수업 학생 리스트 받아볼 수 있음")
    private List<StudentInfo> studentInfos;

    public LectureResponse() {}

    public static LectureResponse createLectureDtoWithEnrollmentInfo(Lecture lecture, List<EnrollmentInfo> enrollmentInfos) {
        LectureResponse lectureResponse = createLectureDto(lecture);
        lectureResponse.studentInfos = enrollmentInfos.stream()
            .map(LectureResponse::createStudentInfo)
            .collect(Collectors.toList());

        return lectureResponse;
    }

    private static StudentInfo createStudentInfo(EnrollmentInfo enrollmentInfo) {
        return new StudentInfo(enrollmentInfo.getMember().getId(),
            enrollmentInfo.getMember().getUnivId(), enrollmentInfo.getMember().getName());
    }

    public static LectureResponse createLectureDto(Lecture lecture) {
        LectureResponse lectureResponse = new LectureResponse();
        lectureResponse.lectureId = lecture.getId();
        lectureResponse.lectureName = lecture.getLectureName();
        lectureResponse.lecturerName = lecture.getLecturerName();
        lectureResponse.lectureState = lecture.getLectureState();
        lectureResponse.studentInfos = new ArrayList<>();
        return lectureResponse;
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
