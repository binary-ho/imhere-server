package gdsc.binaryho.imhere.core.lecture.model.response;

import gdsc.binaryho.imhere.core.lecture.domain.Lecture;
import gdsc.binaryho.imhere.core.lecture.domain.OpenLectures;
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

    public static LectureResponse from(List<Lecture> lectures) {
        List<LectureInfo> lectureInfos = lectures.stream()
            .map(LectureInfo::from)
            .collect(Collectors.toList());

        return new LectureResponse(lectureInfos);
    }

    public static LectureResponse from(OpenLectures openLectures) {
        List<LectureInfo> lectureInfos = openLectures.getOpenLectures()
            .stream()
            .map(LectureInfo::from)
            .collect(Collectors.toList());

        return new LectureResponse(lectureInfos);
    }
}
