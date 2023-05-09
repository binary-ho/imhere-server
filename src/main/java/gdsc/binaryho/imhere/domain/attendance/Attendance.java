package gdsc.binaryho.imhere.domain.attendance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.member.Member;
import java.time.LocalDateTime;
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
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "attendances")
@Getter
@Setter
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

    @JsonIgnore
    @CreatedDate
    private LocalDateTime createdAt;

    public static Attendance createAttendance(Member member, Lecture lecture, String distance,
        String accuracy, LocalDateTime timestamp) {
        Attendance attendance = new Attendance();
        attendance.member = member;
        attendance.lecture = lecture;
        attendance.distance = distance;
        attendance.accuracy = accuracy;
        attendance.timestamp = timestamp;
        return attendance;
    }
}