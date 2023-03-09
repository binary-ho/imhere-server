package gdsc.binaryho.imhere.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JwtAuthenticationFilter(
        AuthenticationManager authenticationManager,
        BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

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
