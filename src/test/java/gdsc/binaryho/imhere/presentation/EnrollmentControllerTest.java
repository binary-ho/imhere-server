package gdsc.binaryho.imhere.presentation;


import static gdsc.binaryho.imhere.mock.fixture.EnrollmentInfoFixture.MOCK_ENROLLMENT_INFO;
import static gdsc.binaryho.imhere.mock.fixture.LectureFixture.MOCK_LECTURE;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gdsc.binaryho.imhere.core.enrollment.application.EnrollmentService;
import gdsc.binaryho.imhere.core.enrollment.controller.EnrollmentController;
import gdsc.binaryho.imhere.core.enrollment.model.response.EnrollmentInfoResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EnrollmentController.class)
public class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnrollmentService enrollmentService;

    @Test
    @WithMockUser
    void 수업의_수강신청_정보를_조회한다() throws Exception {
        long lectureId = 7L;
        EnrollmentInfoResponse enrollmentInfoResponse = EnrollmentInfoResponse.createEnrollmentInfoDto(
            MOCK_LECTURE, List.of(MOCK_ENROLLMENT_INFO));

        given(enrollmentService.getLectureEnrollment(lectureId)).willReturn(enrollmentInfoResponse);

        mockMvc.perform(get("/api/enrollment/" + lectureId)
                .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lectureId").value(MOCK_LECTURE.getId()))
            .andExpect(jsonPath("$.lectureName").value(MOCK_LECTURE.getLectureName()))
            .andExpect(jsonPath("$.lecturerName").value(MOCK_LECTURE.getLecturerName()))

            .andExpect(jsonPath("$.studentInfos").isNotEmpty())
            .andExpect(jsonPath("$.studentInfos[0].id").value(MOCK_ENROLLMENT_INFO.getMember().getId()))
            .andExpect(jsonPath("$.studentInfos[0].univId").value(MOCK_ENROLLMENT_INFO.getMember().getUnivId()))
            .andExpect(jsonPath("$.studentInfos[0].name").value(MOCK_ENROLLMENT_INFO.getMember().getName()))
            .andExpect(jsonPath("$.studentInfos[0].enrollmentState").value(MOCK_ENROLLMENT_INFO.getEnrollmentState().toString()));
    }

    @Test
    @WithMockUser
    void 수강신청_학생을_승인한다() throws Exception {
        long lectureId = 7L;
        long studentId = 77L;
        doNothing().when(enrollmentService).approveStudents(lectureId, studentId);

        mockMvc.perform(post("/api/enrollment/" + lectureId + "/student/" + studentId + "/approval")
                .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void 수강신청_학생을_거절한다() throws Exception {
        long lectureId = 7L;
        long studentId = 77L;
        doNothing().when(enrollmentService).rejectStudents(lectureId, studentId);

        mockMvc.perform(post("/api/enrollment/" + lectureId + "/student/" + studentId + "/rejection")
                .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void 학생이_수강신청을_한다() throws Exception {
        long lectureId = 7L;
        long studentId = 77L;
        doNothing().when(enrollmentService).rejectStudents(lectureId, studentId);

        mockMvc.perform(post("/api/enrollment/" + lectureId + "/student/" + studentId + "/rejection")
                .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk());
    }
}
