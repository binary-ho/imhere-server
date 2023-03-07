package gdsc.binaryho.imhere.domain.attendance;

import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.member.Member;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class Attendance {

    @Id
    @GeneratedValue
    @Column(name = "attendance_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Member member;

    private String distance;
    private String accuracy;
    private LocalDateTime timestamp;

    public static Attendance createAttendance(Lecture lecture, Member member, String distance,
        String accuracy, LocalDateTime timestamp) {
        Attendance attendance = new Attendance();
        attendance.lecture = lecture;
        attendance.member = member;
        attendance.distance = distance;
        attendance.accuracy = accuracy;
        attendance.timestamp = timestamp;
        return attendance;
    }
}
