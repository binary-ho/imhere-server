package gdsc.binaryho.imhere.security.filter;

import gdsc.binaryho.imhere.core.auth.PrincipalDetails;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import gdsc.binaryho.imhere.security.jwt.TokenService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final String HEADER_STRING = "authorization";
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
        if (!checkTokenHeader(request)) {
            chain.doFilter(request, response);
            return;
        }

        String jwtToken = request.getHeader(HEADER_STRING)
            .replace(ACCESS_TOKEN_PREFIX, "");

        if (tokenService.validateTokenExpirationTimeNotExpired(jwtToken)) {

            String univId = tokenService.getUnivId(jwtToken);
            Member member = memberRepository.findByUnivId(univId).orElseThrow();
            PrincipalDetails principalDetails = new PrincipalDetails(member);

            Authentication authentication =
                new UsernamePasswordAuthenticationToken(principalDetails, "", principalDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    private boolean checkTokenHeader(HttpServletRequest request) {
        String jwtHeader = request.getHeader(HEADER_STRING);

        if (jwtHeader == null || !jwtHeader.startsWith(ACCESS_TOKEN_PREFIX)) {
            return false;
        }
        return true;
    }
}
