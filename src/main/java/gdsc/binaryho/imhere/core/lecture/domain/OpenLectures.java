package gdsc.binaryho.imhere.core.lecture.domain;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OpenLectures {

    private final List<OpenLecture> openLectures;

    public List<OpenLecture> getOpenLectures() {
        return List.copyOf(openLectures);
    }

    public boolean isNotEmpty() {
        return !openLectures.isEmpty();
    }
}
