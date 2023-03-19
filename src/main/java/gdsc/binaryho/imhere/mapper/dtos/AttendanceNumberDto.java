package gdsc.binaryho.imhere.mapper.dtos;

import lombok.Getter;

@Getter
public class AttendanceNumberDto {
    private int attendanceNumber;

    public AttendanceNumberDto(int attendanceNumber) {
        this.attendanceNumber = attendanceNumber;
    }
}
