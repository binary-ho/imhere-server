package gdsc.binaryho.imhere.presentation;

import static gdsc.binaryho.imhere.mock.fixture.MemberFixture.MOCK_STUDENT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.binaryho.imhere.core.member.application.MemberService;
import gdsc.binaryho.imhere.core.member.controller.AdminController;
import gdsc.binaryho.imhere.core.member.model.request.ChangeRoleRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser
    void 회원의_권한을_변경한다() throws Exception {
        doNothing().when(memberService).changeMemberRole(any(), any());

        String targetUnivId = MOCK_STUDENT.getUnivId();
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest("LECTURER");

        mockMvc.perform(post("/api/admin/role/" + targetUnivId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeRoleRequest))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }
}
