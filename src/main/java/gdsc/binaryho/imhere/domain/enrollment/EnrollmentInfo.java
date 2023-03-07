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
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "enrollment_infos")
@Getter @Setter
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

    @Column(columnDefinition = "integer default 0")
    private int attendanceCount;

    public static EnrollmentInfo createEnrollmentInfo(Lecture lecture, Member member) {
        EnrollmentInfo enrollmentInfo = new EnrollmentInfo();
        enrollmentInfo.setLecture(lecture);
        enrollmentInfo.setMember(member);
        return enrollmentInfo;
    }
}
