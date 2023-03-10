package gdsc.binaryho.imhere.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.binaryho.imhere.config.auth.PrincipalDetails;
import gdsc.binaryho.imhere.service.TokenService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Value("${jwt.header-string}")
    private String HEADER_STRING;

    @Value("${jwt.access-token-prefix}")
    private String ACCESS_TOKEN_PREFIX;

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenService tokenService;

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            SignInRequest signInRequest = getSignInRequest(request.getInputStream());

            UsernamePasswordAuthenticationToken authenticationToken
                = createUsernamePasswordAuthenticationToken(signInRequest);

            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private UsernamePasswordAuthenticationToken createUsernamePasswordAuthenticationToken(SignInRequest signInRequest) {
        return new UsernamePasswordAuthenticationToken(
            signInRequest.getUnivId(), signInRequest.getPassword());
    }

    /* TODO: 입력 오류 대체 필요 */
    private SignInRequest getSignInRequest(ServletInputStream inputStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SignInRequest signInRequest = objectMapper.readValue(inputStream, SignInRequest.class);
        return encodeSignInRequestPassword(signInRequest);
    }

    private SignInRequest encodeSignInRequestPassword(SignInRequest signInRequest) {
        return new SignInRequest(
            signInRequest.univId, bCryptPasswordEncoder.encode(signInRequest.getPassword()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authResult) {
        System.out.println("successfulAuthentication 실행됨 (인증이 완료됨) ");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        Token jwtToken = tokenService.createToken(principalDetails.getMember().getId(), principalDetails.getMember().getRole());

        response.addHeader(HEADER_STRING, ACCESS_TOKEN_PREFIX + jwtToken.getAccessToken());
    }

    @Getter
    @Setter
    private static class SignInRequest {

        private final String univId;
        private final String password;

        private SignInRequest(String univId, String password) {
            this.univId = univId;
            this.password = password;
        }
    }
}
