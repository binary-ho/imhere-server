package gdsc.binaryho.imhere.domain.attendance;

import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
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
    @JoinColumn(name = "enrollment_id")
    private EnrollmentInfo enrollmentInfo;

    private String distance;
    private String accuracy;
    private LocalDateTime timestamp;

    public static Attendance createAttendance(EnrollmentInfo enrollmentInfo, String distance,
        String accuracy, LocalDateTime timestamp) {
        Attendance attendance = new Attendance();
        attendance.enrollmentInfo = enrollmentInfo;
        attendance.distance = distance;
        attendance.accuracy = accuracy;
        attendance.timestamp = timestamp;
        return attendance;
    }
}
