package gdsc.binaryho.imhere.domain.enrollment;

import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.member.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnrollmentInfo {

    @Id
    @GeneratedValue
    @Column(name = "enrollment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Member member;
    private String studentName;

    @Column(columnDefinition = "integer default 0")
    private int attendanceCount;

    public static EnrollmentInfo createEnrollmentInfo(Lecture lecture, Member member) {
        EnrollmentInfo enrollmentInfo = new EnrollmentInfo();
        enrollmentInfo.setLecture(lecture);
        enrollmentInfo.setMember(member);
        enrollmentInfo.setStudentName(member.getName());
        return enrollmentInfo;
    }
}
