package gdsc.binaryho.imhere.security;

import static gdsc.binaryho.imhere.mock.fixture.MemberFixture.MOCK_STUDENT;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import gdsc.binaryho.imhere.security.jwt.Token;
import gdsc.binaryho.imhere.security.jwt.TokenPropertyHolder;
import gdsc.binaryho.imhere.security.jwt.TokenService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @MockBean
    private MemberRepository memberRepository;

    @Autowired
    private TokenPropertyHolder tokenPropertyHolder;

    @Test
    public void 인증이_필요한_경로에_접근하면_깃허브_로그인_페이지로_Redirection_된다() throws Exception {
        mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location", containsString("/oauth2/authorization/github")));
    }

    @Test
    public void 토큰을_통해_인가_할_수_있다() throws Exception {
        given(memberRepository.findById(any()))
            .willReturn(Optional.of(MOCK_STUDENT));
        Token token = tokenService.createToken(1L, Role.STUDENT);

        String accessTokenPrefix = tokenPropertyHolder.getAccessTokenPrefix();
        mockMvc.perform(get("/api/lecture")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, accessTokenPrefix + token.getAccessToken())
            )
            .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void 권한이_없는_토큰_요청은_403_응답을_반환한다() throws Exception {
        given(memberRepository.findById(any()))
            .willReturn(Optional.of(MOCK_STUDENT));
        Token token = tokenService.createToken(1L, Role.STUDENT);

        String accessTokenPrefix = tokenPropertyHolder.getAccessTokenPrefix();
        mockMvc.perform(post("/api/admin/role/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, accessTokenPrefix + token.getAccessToken())
            )
            .andExpect(status().isForbidden());
    }
}
