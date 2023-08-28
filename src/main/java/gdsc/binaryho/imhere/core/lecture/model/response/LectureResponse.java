package gdsc.binaryho.imhere.core.lecture.model.response;

import gdsc.binaryho.imhere.core.lecture.domain.Lecture;
import gdsc.binaryho.imhere.core.lecture.domain.OpenLecture;
import gdsc.binaryho.imhere.core.lecture.model.LectureInfo;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class LectureResponse {

    private final List<LectureInfo> lectureInfos;

    public LectureResponse(List<LectureInfo> lectureInfos) {
        this.lectureInfos = lectureInfos;
    }

    public static LectureResponse createLectureResponseFromLectures(List<Lecture> lectures) {
        List<LectureInfo> lectureInfos = lectures.stream()
            .map(LectureInfo::from)
            .collect(Collectors.toList());

        return new LectureResponse(lectureInfos);
    }

    public static LectureResponse fromOpenLectures(List<OpenLecture> openLectures) {
        List<LectureInfo> lectureInfos = openLectures.stream()
            .map(LectureInfo::from)
            .collect(Collectors.toList());

        return new LectureResponse(lectureInfos);
    }
}
