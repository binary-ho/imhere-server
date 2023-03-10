package gdsc.binaryho.imhere.config.jwt;

import gdsc.binaryho.imhere.domain.member.SignInRequest;
import gdsc.binaryho.imhere.domain.member.SignInResponseDto;
import gdsc.binaryho.imhere.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final MemberService memberService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            System.out.println("JwtAuthenticationProvider 진입");
            UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authentication;
            String principal = (String) authenticationToken.getPrincipal();
            String credential = (String) authenticationToken.getCredentials();

            SignInResponseDto signInResponseDto = memberService.login(new SignInRequest(principal, credential));

            return new UsernamePasswordAuthenticationToken(
                signInResponseDto.getUnivId(), null,
                AuthorityUtils.createAuthorityList(signInResponseDto.getRoleKey())
            );
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return ClassUtils.isAssignable(UsernamePasswordAuthenticationToken.class, authentication);
    }
}
