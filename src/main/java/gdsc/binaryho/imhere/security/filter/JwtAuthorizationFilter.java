package gdsc.binaryho.imhere.security.filter;

import gdsc.binaryho.imhere.core.auth.exception.MemberNotFoundException;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import gdsc.binaryho.imhere.security.jwt.TokenService;
import gdsc.binaryho.imhere.security.principal.PrincipalDetails;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final String TOKEN_HEADER_STRING = HttpHeaders.AUTHORIZATION;
    private static final String ACCESS_TOKEN_PREFIX = "Token ";

    private final TokenService tokenService;
    private final MemberRepository memberRepository;

    public JwtAuthorizationFilter(
        AuthenticationManager authenticationManager,
        TokenService tokenService, MemberRepository memberRepository) {
        super(authenticationManager);
        this.tokenService = tokenService;
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String jwtToken = request.getHeader(TOKEN_HEADER_STRING);
        if (isTokenNullOrInvalidate(jwtToken)) {
            chain.doFilter(request, response);
            return;
        }

        String tokenValue = jwtToken.replace(ACCESS_TOKEN_PREFIX, "");
        if (tokenService.validateTokenExpirationTimeNotExpired(tokenValue)) {
            setAuthentication(tokenValue);
        }
        chain.doFilter(request, response);
    }

    private boolean isTokenNullOrInvalidate(String token) {
        return Objects.isNull(token)
            || (!token.startsWith(ACCESS_TOKEN_PREFIX));
    }

    private void setAuthentication(String jwtToken) {
        String univId = tokenService.getUnivId(jwtToken);
        Member member = memberRepository.findByUnivId(univId)
            .orElseThrow(() -> MemberNotFoundException.EXCEPTION);

        PrincipalDetails principalDetails = new PrincipalDetails(member);
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(principalDetails, "", principalDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
