package gdsc.binaryho.imhere.core.lecture.model.response;

import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.lecture.Lecture;
import gdsc.binaryho.imhere.core.lecture.LectureState;
import gdsc.binaryho.imhere.core.lecture.model.OpenLecture;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class LectureResponse {

    private final List<LectureInfo> lectureInfos;

    private LectureResponse(List<LectureInfo> lectureInfos) {
        this.lectureInfos = lectureInfos;
    }

    public static LectureResponse createLectureResponseFromLectures(List<Lecture> lectures) {
        List<LectureInfo> lectureInfos = lectures.stream()
            .map(LectureInfo::new)
            .collect(Collectors.toList());
        return new LectureResponse(lectureInfos);
    }

    public static LectureResponse createLectureResponseFromEnrollmentInfos(
        List<List<EnrollmentInfo>> lecturerEnrollmentInfos) {
        List<LectureInfo> lectureInfos = lecturerEnrollmentInfos.stream()
            .map(LectureInfo::new)
            .collect(Collectors.toList());
        return new LectureResponse(lectureInfos);
    }

    public static LectureResponse from(List<OpenLecture> openLectures) {
        List<LectureInfo> lectureInfos = openLectures.stream()
            .map(openLecture -> new LectureInfo(openLecture.getId(), openLecture.getName(),
                    openLecture.getLecturerName(), LectureState.OPEN, new ArrayList<>()))
            .collect(Collectors.toList());
        return new LectureResponse(lectureInfos);
    }

    @Getter
    @Tag(name = "LectureDto", description = "수업 정보")
    @AllArgsConstructor
    public static class LectureInfo {

        private final Long lectureId;
        private final String lectureName;
        private final String lecturerName;
        private final LectureState lectureState;
        @Schema(description = "강사의 경우 본인 수업 학생 리스트 받아볼 수 있음")
        private final List<StudentInfo> studentInfos;

        private LectureInfo(Lecture lecture) {
            this.lectureId = lecture.getId();
            this.lectureName = lecture.getLectureName();
            this.lecturerName = lecture.getLecturerName();
            this.lectureState = lecture.getLectureState();
            this.studentInfos = new ArrayList<>();
        }

        private LectureInfo(List<EnrollmentInfo> enrollmentInfos) {
            Lecture anyLecture = enrollmentInfos.get(0).getLecture();
            this.lectureId = anyLecture.getId();
            this.lectureName = anyLecture.getLectureName();
            this.lecturerName = anyLecture.getLecturerName();
            this.lectureState = anyLecture.getLectureState();
            this.studentInfos = enrollmentInfos.stream()
                .map(LectureInfo::createStudentInfo)
                .collect(Collectors.toList());
        }

        private static StudentInfo createStudentInfo(EnrollmentInfo enrollmentInfo) {
            return new StudentInfo(enrollmentInfo.getMember().getId(),
                enrollmentInfo.getMember().getUnivId(), enrollmentInfo.getMember().getName());
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
        }
    }
}
