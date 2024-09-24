package gdsc.binaryho.imhere.core.attendance.application;

import gdsc.binaryho.imhere.core.attendance.domain.Attendance;
import gdsc.binaryho.imhere.core.attendance.infrastructure.AttendanceRepository;
import gdsc.binaryho.imhere.core.lecture.domain.Lecture;
import gdsc.binaryho.imhere.core.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AttendanceSaveService {

    private final AttendanceRepository attendanceRepository;

    @Transactional
    public void save(Attendance attendance) {
        attendanceRepository.save(attendance);
        logAttendanceHistory(attendance);
    }

    private void logAttendanceHistory(Attendance attendance) {
        Member student = attendance.getStudent();
        Lecture lecture = attendance.getLecture();
        log.info("[출석 완료] {}({}) , 학생 : {} ({})",
            lecture::getLectureName, lecture::getId,
            student::getUnivId, student::getName);
    }
}
