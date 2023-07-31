package gdsc.binaryho.imhere.core.enrollment;

import gdsc.binaryho.imhere.core.lecture.Lecture;
import gdsc.binaryho.imhere.core.member.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "enrollment_infos")
@Getter @Setter
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

    @Column(columnDefinition = "integer default 0")
    private int attendanceCount;

    @Enumerated(EnumType.STRING)
    private EnrollmentState enrollmentState;

    public static EnrollmentInfo createEnrollmentInfo(Lecture lecture, Member member, EnrollmentState enrollmentState) {
        EnrollmentInfo enrollmentInfo = new EnrollmentInfo();
        enrollmentInfo.setLecture(lecture);
        enrollmentInfo.setMember(member);
        enrollmentInfo.setEnrollmentState(enrollmentState);
        return enrollmentInfo;
    }
}
