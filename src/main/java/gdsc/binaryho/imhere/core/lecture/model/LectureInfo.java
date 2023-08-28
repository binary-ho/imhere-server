package gdsc.binaryho.imhere.core.lecture.model;

import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.lecture.LectureState;
import gdsc.binaryho.imhere.core.lecture.domain.Lecture;
import gdsc.binaryho.imhere.core.lecture.domain.OpenLecture;
import gdsc.binaryho.imhere.core.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Tag(name = "LectureDto", description = "수업 정보")
@Builder
public class LectureInfo {

    private final Long lectureId;
    private final String lectureName;
    private final String lecturerName;
    private final LectureState lectureState;
    @Schema(description = "강사의 경우 본인 수업 학생 리스트 받아볼 수 있음")
    private final List<StudentInfo> studentInfos;

    public static LectureInfo from(Lecture lecture) {
        return LectureInfo.builder()
            .lectureId(lecture.getId())
            .lectureName(lecture.getLectureName())
            .lecturerName(lecture.getLecturerName())
            .lectureState(lecture.getLectureState())
            .studentInfos(Collections.emptyList())
            .build();
    }

    public static LectureInfo from(OpenLecture openLecture) {
        return LectureInfo.builder()
            .lectureId(openLecture.getId())
            .lectureName(openLecture.getName())
            .lecturerName(openLecture.getLecturerName())
            .lectureState(LectureState.OPEN)
            .studentInfos(Collections.emptyList())
            .build();
    }

    public static LectureInfo from(Lecture lecture, List<EnrollmentInfo> enrollmentInfos) {
        List<StudentInfo> studentInfos = enrollmentInfos
            .stream()
            .map(EnrollmentInfo::getMember)
            .map(StudentInfo::from)
            .collect(Collectors.toList());

        return LectureInfo.builder()
            .lectureId(lecture.getId())
            .lectureName(lecture.getLectureName())
            .lecturerName(lecture.getLecturerName())
            .lectureState(lecture.getLectureState())
            .studentInfos(studentInfos)
            .build();
    }

    @Getter
    @Tag(name = "StudentInfo", description = "학생 정보")
    public static class StudentInfo {

        private final Long id;
        private final String univId;
        private final String name;

        private StudentInfo(Long id, String univId, String name) {
            this.id = id;
            this.univId = univId;
            this.name = name;
        }

        private static StudentInfo from(Member member) {
            return new StudentInfo(member.getId(), member.getUnivId(), member.getName());
        }
    }
}
