package gdsc.binaryho.imhere.mapper.dtos;

import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureState;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class LectureDto {

    private Long lectureId;
    private String lectureName;
    private String lecturerName;
    private LectureState lectureState;
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
}
