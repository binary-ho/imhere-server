package gdsc.binaryho.imhere.config.jwt;

import gdsc.binaryho.imhere.config.auth.PrincipalDetails;
import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.MemberRepository;
import gdsc.binaryho.imhere.service.TokenService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    @Value("${jwt.header-string}")
    private String HEADER_STRING;

    @Value("${jwt.access-token-prefix}")
    private String ACCESS_TOKEN_PREFIX;

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

        if (tokenService.validateTokenExpirationTime(jwtToken)) {

            long memberId = tokenService.getMemberId(jwtToken);
            Member member = memberRepository.findById(memberId).orElseThrow();
            PrincipalDetails principalDetails = new PrincipalDetails(member);

            Authentication authentication =
                new UsernamePasswordAuthenticationToken(principalDetails, "", principalDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    private boolean checkTokenHeader(HttpServletRequest request) {
        String jwtHeader = request.getHeader(HEADER_STRING);

        if (jwtHeader.isEmpty() || !jwtHeader.startsWith(ACCESS_TOKEN_PREFIX)) {
            return false;
        }
        return true;
    }
}
