package gdsc.binaryho.imhere.presentation;

import static gdsc.binaryho.imhere.mock.fixture.MemberFixture.MOCK_STUDENT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.binaryho.imhere.core.auth.application.AuthService;
import gdsc.binaryho.imhere.core.auth.application.EmailVerificationService;
import gdsc.binaryho.imhere.core.auth.controller.AuthController;
import gdsc.binaryho.imhere.core.auth.model.request.ChangePasswordRequest;
import gdsc.binaryho.imhere.core.auth.model.request.SendPasswordChangeEmailRequest;
import gdsc.binaryho.imhere.core.auth.model.request.SendSignUpEmailRequest;
import gdsc.binaryho.imhere.core.auth.model.request.SignUpRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private EmailVerificationService emailVerificationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser
    void 회원가입을_한다() throws Exception {
        doNothing().when(authService).signUp(any(), any(), any());

        SignUpRequest signUpRequest = new SignUpRequest(MOCK_STUDENT.getUnivId(),
            MOCK_STUDENT.getName(), MOCK_STUDENT.getPassword());

        mockMvc.perform(post("/member/new")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void 회원가입을_위해_인증_코드가_담긴_이메일을_발급_받는다() throws Exception {
        doNothing().when(authService).sendSignUpEmail(any());

        SendSignUpEmailRequest sendSignUpEmailRequest = new SendSignUpEmailRequest(
            MOCK_STUDENT.getUnivId());

        mockMvc.perform(post("/member/verification?" + "type=" + "sign-up")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendSignUpEmailRequest))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void 비밀번호_변경을_위해_인증_코드가_담긴_이메일을_발급_받는다() throws Exception {
        doNothing().when(authService).sendPasswordChangeEmail(any());

        SendPasswordChangeEmailRequest sendPasswordChangeEmailRequest = new SendPasswordChangeEmailRequest(
            MOCK_STUDENT.getUnivId());

        mockMvc.perform(post("/member/verification?" + "type=" + "password-change")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendPasswordChangeEmailRequest))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void 회원이_제출한_인증_코드와_발급_받은_코드의_일치_여부를_확인한다() throws Exception {
        doNothing().when(emailVerificationService).verifyCode(any(), any());

        String email = MOCK_STUDENT.getUnivId();
        String verificationCode = "Verification Code";

        mockMvc.perform(get("/member/verification/" + email + "/" + verificationCode)
                .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void 비밀번호를_변경한다() throws Exception {
        doNothing().when(emailVerificationService).verifyCode(any(), any());

        String email = MOCK_STUDENT.getUnivId();
        String verificationCode = "Verification Code";
        String newPassword = "Password";
        String confirmationPassword = "Password";

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(email,
            verificationCode, newPassword, confirmationPassword);

        mockMvc.perform(post("/member/password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }
}
