package gdsc.binaryho.imhere.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.binaryho.imhere.security.jwt.Token;
import gdsc.binaryho.imhere.security.jwt.TokenService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String HEADER_STRING = HttpHeaders.AUTHORIZATION;
    private static final String ACCESS_TOKEN_PREFIX = "Token ";

    private final AuthenticationManager authenticationManager;
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
            return super.attemptAuthentication(request, response);
        }
    }

    private UsernamePasswordAuthenticationToken createUsernamePasswordAuthenticationToken(SignInRequest signInRequest) {
        return new UsernamePasswordAuthenticationToken(
            signInRequest.getUnivId(), signInRequest.getPassword());
    }

    private SignInRequest getSignInRequest(ServletInputStream inputStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(inputStream, SignInRequest.class);
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authResult) {

        String grantedAuthority = authResult.getAuthorities().stream().findAny().orElseThrow().toString();
        Token jwtToken = tokenService.createToken(authResult.getPrincipal().toString(), grantedAuthority);

        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.AUTHORIZATION);
        response.addHeader(HEADER_STRING, ACCESS_TOKEN_PREFIX + jwtToken.getAccessToken());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class SignInRequest {

        private String univId;
        private String password;
    }
}
