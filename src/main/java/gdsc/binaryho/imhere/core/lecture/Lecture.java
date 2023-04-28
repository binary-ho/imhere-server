package gdsc.binaryho.imhere.core.lecture;


import gdsc.binaryho.imhere.core.member.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lectures")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lecturer_id")
    private Member member;

    private String lectureName;
    private String lecturerName;

    @Enumerated(EnumType.STRING)
    private LectureState lectureState;

    public static Lecture createLecture(Member lecturer, String lectureName) {
        Lecture lecture = new Lecture();
        lecture.setMember(lecturer);
        lecture.setLectureName(lectureName);
        lecture.setLecturerName(lecturer.getName());
        lecture.setLectureState(LectureState.CLOSED);
        return lecture;
    }
}
