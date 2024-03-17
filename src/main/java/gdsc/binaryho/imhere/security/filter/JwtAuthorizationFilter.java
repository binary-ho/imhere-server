package gdsc.binaryho.imhere.security.filter;

import gdsc.binaryho.imhere.core.auth.exception.MemberNotFoundException;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import gdsc.binaryho.imhere.security.jwt.TokenPropertyHolder;
import gdsc.binaryho.imhere.security.jwt.TokenUtil;
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

    private final TokenUtil tokenUtil;
    private final MemberRepository memberRepository;
    private final TokenPropertyHolder tokenPropertyHolder;

    public JwtAuthorizationFilter(
        AuthenticationManager authenticationManager,
        TokenUtil tokenUtil, MemberRepository memberRepository,
        TokenPropertyHolder tokenPropertyHolder) {
        super(authenticationManager);
        this.tokenUtil = tokenUtil;
        this.memberRepository = memberRepository;
        this.tokenPropertyHolder = tokenPropertyHolder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain)
        throws ServletException, IOException {
        String jwtToken = request.getHeader(TOKEN_HEADER_STRING);
        if (isTokenNullOrInvalidate(jwtToken)) {
            chain.doFilter(request, response);
            return;
        }

        String accessTokenPrefix = tokenPropertyHolder.getAccessTokenPrefix();
        String tokenValue = jwtToken.replace(accessTokenPrefix, "");
        if (tokenUtil.validateTokenExpirationTimeNotExpired(tokenValue)) {
            setAuthentication(tokenValue);
        }
        chain.doFilter(request, response);
    }

    private boolean isTokenNullOrInvalidate(String token) {
        String accessTokenPrefix = tokenPropertyHolder.getAccessTokenPrefix();
        return Objects.isNull(token)
            || (!token.startsWith(accessTokenPrefix));
    }

    private void setAuthentication(String jwtToken) {
        Long id = tokenUtil.getId(jwtToken);
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> MemberNotFoundException.EXCEPTION);

        PrincipalDetails principalDetails = new PrincipalDetails(member);
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(principalDetails, "",
                principalDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
