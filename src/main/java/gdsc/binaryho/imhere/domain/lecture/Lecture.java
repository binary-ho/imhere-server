package gdsc.binaryho.imhere.domain.lecture;


import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.domain.member.Member;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lectures")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) /* TODO */
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lecturer_id")
    private Member member;

    private String lectureName;
    private String lecturerName;
    private Integer attendanceNumber;

    @Enumerated(EnumType.STRING)
    private LectureState lectureState;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL)
    private List<EnrollmentInfo> enrollmentInfos = new ArrayList<>();

    public static Lecture createLecture(Member lecturer, String lectureName) {
        Lecture lecture = new Lecture();
        lecture.setMember(lecturer);
        lecture.setLectureName(lectureName);
        lecture.setLecturerName(lecturer.getName());
        lecture.setLectureState(LectureState.CLOSED);
        lecture.setAttendanceNumber(0);
        return lecture;
    }
}
