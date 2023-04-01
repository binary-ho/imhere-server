package gdsc.binaryho.imhere.mapper.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttendanceRequest {

    @Schema(description = "출석 번호")
    private Integer attendanceNumber;
    @Schema(description = "홍익대학교 T동과의 거리로 m 단위입니다.")
    private String distance;
    @Schema(description = "Geolocation API가 제공하는 거리 오차 정확도로 m 단위입니다.")
    private String accuracy;
    @Schema(description = "밀리 세컨즈 출석 시각 js Date 객체의 getTime으로 얻어낸 값")
    private Long milliseconds;
}
