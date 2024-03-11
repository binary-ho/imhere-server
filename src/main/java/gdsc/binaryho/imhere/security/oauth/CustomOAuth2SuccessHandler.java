package gdsc.binaryho.imhere.security.oauth;

import gdsc.binaryho.imhere.security.SignUpProcessRedirectionPath;
import gdsc.binaryho.imhere.security.jwt.Token;
import gdsc.binaryho.imhere.security.jwt.TokenService;
import gdsc.binaryho.imhere.util.ClientUrlUtil;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String HEADER_STRING = HttpHeaders.AUTHORIZATION;
    private static final String ACCESS_TOKEN_PREFIX = "Token ";

    private final TokenService tokenService;
    private final ClientUrlUtil clientUrlUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        setRedirectUrl(request, response, customOAuth2User.getSignUpProcessRedirectionPath());
        setAccessToken(response, customOAuth2User);
    }

    private void setRedirectUrl(HttpServletRequest request, HttpServletResponse response,
        SignUpProcessRedirectionPath signupProcessRedirectionPath) throws IOException {
        String redirectUrl = clientUrlUtil.getClientUrl() + signupProcessRedirectionPath.getRedirectUrlPath();
        this.getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private void setAccessToken(HttpServletResponse response, CustomOAuth2User oAuthUser) {
        Token jwtToken = tokenService.createToken(oAuthUser.getMemberId(), oAuthUser.getRole());
        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.AUTHORIZATION);
        response.addHeader(HEADER_STRING, ACCESS_TOKEN_PREFIX + jwtToken.getAccessToken());
    }
}