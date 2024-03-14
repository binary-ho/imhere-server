package gdsc.binaryho.imhere.presentation;

import static gdsc.binaryho.imhere.mock.fixture.AttendanceFixture.ACCURACY;
import static gdsc.binaryho.imhere.mock.fixture.AttendanceFixture.ATTENDANCE_NUMBER;
import static gdsc.binaryho.imhere.mock.fixture.AttendanceFixture.DISTANCE;
import static gdsc.binaryho.imhere.mock.fixture.AttendanceFixture.MILLISECONDS;
import static gdsc.binaryho.imhere.mock.fixture.AttendanceFixture.MOCK_ATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import gdsc.binaryho.imhere.core.attendance.application.LecturerAttendanceService;
import gdsc.binaryho.imhere.core.attendance.application.StudentAttendanceService;
import gdsc.binaryho.imhere.core.attendance.model.response.StudentRecentAttendanceResponse;
import gdsc.binaryho.imhere.core.attendance.controller.AttendanceController;
import gdsc.binaryho.imhere.core.attendance.model.request.AttendanceRequest;
import gdsc.binaryho.imhere.core.attendance.model.response.LecturerAttendanceResponse;
import gdsc.binaryho.imhere.core.attendance.model.response.StudentAttendanceResponse;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(AttendanceController.class)
public class AttendanceControllerTest {

    private static final String ROLE_PARAM = "role=";
    private static final String LECTURER_ROLE_PARAM = ROLE_PARAM + "lecturer";
    private static final String STUDENT_ROLE_PARAM = ROLE_PARAM + "student";
    private static final String RECENT_PARAM = "status=recent";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentAttendanceService studentAttendanceService;

    @MockBean
    private LecturerAttendanceService lecturerAttendanceService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser
    void 학생이_출석한다() throws Exception {

        long lectureId = 7L;
        AttendanceRequest attendanceRequest = new AttendanceRequest(ATTENDANCE_NUMBER, DISTANCE,
            ACCURACY, MILLISECONDS);

        doNothing().when(studentAttendanceService).takeAttendance(attendanceRequest, lectureId);

        mockMvc.perform(post(String.format("/api/lecture/%d/attendance?" + STUDENT_ROLE_PARAM, lectureId))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(attendanceRequest))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void 학생은_최근_출석_정보를_조회할_수_있다() throws Exception {
        long lectureId = 7L;
        long milliseconds = MOCK_ATTENDANCE.getTimestamp().toInstant(ZoneOffset.UTC).toEpochMilli();
        StudentRecentAttendanceResponse response = new StudentRecentAttendanceResponse(
            List.of(MOCK_ATTENDANCE.getTimestamp().toString()));
        given(studentAttendanceService.getStudentRecentAttendance(lectureId))
            .willReturn(response);

        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format("/api/lecture/%d/attendance?" + STUDENT_ROLE_PARAM + "&" + RECENT_PARAM, lectureId, milliseconds))
                    .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk())

            .andExpect(jsonPath("$.timestamps").isNotEmpty())
            .andExpect(result -> {
                String timestamp = JsonPath.read(result.getResponse().getContentAsString(),
                    "$.timestamps[0]");
                assertThat(MOCK_ATTENDANCE.getTimestamp().toString()).contains(timestamp);
            });
    }

    @Test
    @WithMockUser
    void 학생은_지정_날짜의_출석_정보를_조회할_수_있다() throws Exception {
        long lectureId = 7L;
        long milliseconds = MOCK_ATTENDANCE.getTimestamp().toInstant(ZoneOffset.UTC).toEpochMilli();
        StudentAttendanceResponse response = new StudentAttendanceResponse(
            List.of(MOCK_ATTENDANCE));
        given(studentAttendanceService.getStudentDayAttendance(lectureId, milliseconds))
            .willReturn(response);

        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format("/api/lecture/%d/attendance?timestamp=%d&" + STUDENT_ROLE_PARAM, lectureId, milliseconds))
                    .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk())

            .andExpect(jsonPath("$.attendances").isNotEmpty())
            .andExpect(
                jsonPath("$.attendances[0].distance").value(MOCK_ATTENDANCE.getDistance()))
            .andExpect(
                jsonPath("$.attendances[0].accuracy").value(MOCK_ATTENDANCE.getAccuracy()))

            .andExpect(result -> {
                String timestamp = JsonPath.read(result.getResponse().getContentAsString(),
                    "$.attendances[0].timestamp");
                assertThat(MOCK_ATTENDANCE.getTimestamp().toString()).contains(timestamp);
            });
    }

    @Test
    @WithMockUser
    void 강사는_자신의_강의의_전체_출석_정보를_조회할_수_있다() throws Exception {
        long lectureId = 7L;
        LecturerAttendanceResponse lecturerAttendanceResponse = new LecturerAttendanceResponse(
            List.of(MOCK_ATTENDANCE));
        given(lecturerAttendanceService.getLecturerAttendances(lectureId)).willReturn(lecturerAttendanceResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/api/lecture/%d/attendance?" + LECTURER_ROLE_PARAM, lectureId))
                .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk())

            .andExpect(jsonPath("$.attendances").isNotEmpty())
            .andExpect(jsonPath("$.attendances[0].univId").value(
                MOCK_ATTENDANCE.getStudent().getUnivId()))
            .andExpect(
                jsonPath("$.attendances[0].name").value(MOCK_ATTENDANCE.getStudent().getName()))
            .andExpect(
                jsonPath("$.attendances[0].distance").value(MOCK_ATTENDANCE.getDistance()))
            .andExpect(
                jsonPath("$.attendances[0].accuracy").value(MOCK_ATTENDANCE.getAccuracy()))
            .andExpect(result -> {
                String timestamp = JsonPath.read(result.getResponse().getContentAsString(),
                    "$.attendances[0].timestamp");
                assertThat(MOCK_ATTENDANCE.getTimestamp().toString()).contains(timestamp);
            });
    }

    @Test
    @WithMockUser
    void 강사는_특정_날짜의_출석_정보를_조회할_수_있다() throws Exception {
        long lectureId = 7L;
        long milliseconds = MOCK_ATTENDANCE.getTimestamp().toInstant(ZoneOffset.UTC).toEpochMilli();
        LecturerAttendanceResponse lecturerAttendanceResponse = new LecturerAttendanceResponse(
            List.of(MOCK_ATTENDANCE));
        given(lecturerAttendanceService.getLecturerDayAttendances(lectureId, milliseconds)).willReturn(
            lecturerAttendanceResponse);

        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format("/api/lecture/%d/attendance?timestamp=%d&" + LECTURER_ROLE_PARAM, lectureId, milliseconds))
                    .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk())

            .andExpect(jsonPath("$.attendances").isNotEmpty())
            .andExpect(jsonPath("$.attendances[0].univId").value(
                MOCK_ATTENDANCE.getStudent().getUnivId()))
            .andExpect(
                jsonPath("$.attendances[0].name").value(MOCK_ATTENDANCE.getStudent().getName()))
            .andExpect(
                jsonPath("$.attendances[0].distance").value(MOCK_ATTENDANCE.getDistance()))
            .andExpect(
                jsonPath("$.attendances[0].accuracy").value(MOCK_ATTENDANCE.getAccuracy()))

            .andExpect(result -> {
                String timestamp = JsonPath.read(result.getResponse().getContentAsString(),
                    "$.attendances[0].timestamp");
                assertThat(MOCK_ATTENDANCE.getTimestamp().toString()).contains(timestamp);
            });
    }
}
