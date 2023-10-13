package gdsc.binaryho.imhere.presentation;

import static gdsc.binaryho.imhere.mock.fixture.EnrollmentInfoFixture.MOCK_ENROLLMENT_INFO;
import static gdsc.binaryho.imhere.mock.fixture.LectureFixture.MOCK_LECTURE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.binaryho.imhere.core.lecture.application.LectureService;
import gdsc.binaryho.imhere.core.lecture.controller.LectureController;
import gdsc.binaryho.imhere.core.lecture.model.LectureInfo;
import gdsc.binaryho.imhere.core.lecture.model.request.LectureCreateRequest;
import gdsc.binaryho.imhere.core.lecture.model.response.AttendanceNumberResponse;
import gdsc.binaryho.imhere.core.lecture.model.response.LectureResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LectureController.class)
public class LectureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LectureService lectureService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser
    void 학생이_수강신청을_위해_개설된_강의_리스트를_조회한다() throws Exception {
        LectureInfo lectureInfo = LectureInfo.from(MOCK_LECTURE);
        LectureResponse lectureResponse = new LectureResponse(
            List.of(lectureInfo));

        given(lectureService.getAllLecturesForEnrollment()).willReturn(lectureResponse);

        mockMvc.perform(get("/api/lecture")
                .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lectureInfos[0].lectureId").value(MOCK_LECTURE.getId()))
            .andExpect(jsonPath("$.lectureInfos[0].lectureName").value(MOCK_LECTURE.getLectureName()))
            .andExpect(jsonPath("$.lectureInfos[0].lecturerName").value(MOCK_LECTURE.getLecturerName()))
            .andExpect(jsonPath("$.lectureInfos[0].lectureState").value(MOCK_LECTURE.getLectureState().toString()))
            .andExpect(jsonPath("$.lectureInfos[0].studentInfos").isEmpty());
    }

    @Test
    @WithMockUser
    void 학생이_자신이_수강중인_강의_리스트를_조회한다() throws Exception {
        LectureInfo lectureInfo = LectureInfo.from(MOCK_LECTURE);
        LectureResponse lectureResponse = new LectureResponse(
            List.of(lectureInfo));

        given(lectureService.getStudentLectures()).willReturn(lectureResponse);

        mockMvc.perform(get("/api/lecture" + "?status=" + "enrolled")
                .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lectureInfos[0].lectureId").value(MOCK_LECTURE.getId()))
            .andExpect(jsonPath("$.lectureInfos[0].lectureName").value(MOCK_LECTURE.getLectureName()))
            .andExpect(jsonPath("$.lectureInfos[0].lecturerName").value(MOCK_LECTURE.getLecturerName()))
            .andExpect(jsonPath("$.lectureInfos[0].lectureState").value(MOCK_LECTURE.getLectureState().toString()))
            .andExpect(jsonPath("$.lectureInfos[0].studentInfos").isEmpty());
    }

    @Test
    @WithMockUser
    void 학생이_출석_가능한_강의_리스트를_조회한다() throws Exception {
        LectureInfo lectureInfo = LectureInfo.from(MOCK_LECTURE);
        LectureResponse lectureResponse = new LectureResponse(
            List.of(lectureInfo));

        given(lectureService.getStudentOpenLectures()).willReturn(lectureResponse);

        mockMvc.perform(get("/api/lecture" + "?status=" + "opened")
                .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lectureInfos[0].lectureId").value(MOCK_LECTURE.getId()))
            .andExpect(jsonPath("$.lectureInfos[0].lectureName").value(MOCK_LECTURE.getLectureName()))
            .andExpect(jsonPath("$.lectureInfos[0].lecturerName").value(MOCK_LECTURE.getLecturerName()))
            .andExpect(jsonPath("$.lectureInfos[0].lectureState").value(MOCK_LECTURE.getLectureState().toString()))
            .andExpect(jsonPath("$.lectureInfos[0].studentInfos").isEmpty());
    }

    @Test
    @WithMockUser
    void 강사가_자신이_개설한_강의와_학생_목록을_조회한다() throws Exception {
        LectureInfo lectureInfo = LectureInfo.from(MOCK_LECTURE, List.of(MOCK_ENROLLMENT_INFO));
        LectureResponse lectureResponse = new LectureResponse(
            List.of(lectureInfo));

        given(lectureService.getOwnedLectures()).willReturn(lectureResponse);

        mockMvc.perform(get("/api/lecture" + "?status=" + "owned")
                .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lectureInfos[0].lectureId").value(MOCK_LECTURE.getId()))
            .andExpect(jsonPath("$.lectureInfos[0].lectureName").value(MOCK_LECTURE.getLectureName()))
            .andExpect(jsonPath("$.lectureInfos[0].lecturerName").value(MOCK_LECTURE.getLecturerName()))
            .andExpect(jsonPath("$.lectureInfos[0].lectureState").value(MOCK_LECTURE.getLectureState().toString()))

            .andExpect(jsonPath("$.lectureInfos[0].studentInfos").isNotEmpty())
            .andExpect(jsonPath("$.lectureInfos[0].studentInfos[0].id").value(MOCK_ENROLLMENT_INFO.getMember().getId()))
            .andExpect(jsonPath("$.lectureInfos[0].studentInfos[0].univId").value(MOCK_ENROLLMENT_INFO.getMember().getUnivId()))
            .andExpect(jsonPath("$.lectureInfos[0].studentInfos[0].name").value(MOCK_ENROLLMENT_INFO.getMember().getName()));
    }

    @Test
    @WithMockUser
    void 강의를_생성한다() throws Exception {
        doNothing().when(lectureService).createLecture(any());
        LectureCreateRequest lectureCreateRequest = new LectureCreateRequest("lectureName");

        mockMvc.perform(post("/api/lecture")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lectureCreateRequest))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void 강의를_OPEN_하고_출석_번호를_발급_받는다() throws Exception {
        long lectureId = 7L;
        AttendanceNumberResponse attendanceNumberResponse = new AttendanceNumberResponse(7777);
        given(lectureService.openLectureAndGenerateAttendanceNumber(any())).willReturn(attendanceNumberResponse);

        mockMvc.perform(post("/api/lecture/" + lectureId + "/open")
                .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.attendanceNumber").value(7777));
    }

    @Test
    @WithMockUser
    void 강의를_닫는다() throws Exception {
        long lectureId = 7L;

        mockMvc.perform(post("/api/lecture/" + lectureId + "/close")
                .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk());
    }
}
