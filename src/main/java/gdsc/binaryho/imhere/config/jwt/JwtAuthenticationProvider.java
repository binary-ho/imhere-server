package gdsc.binaryho.imhere.config.jwt;

import gdsc.binaryho.imhere.mapper.dtos.SignInRequestValidationResult;
import gdsc.binaryho.imhere.mapper.requests.SignInRequest;
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
            UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authentication;
            String principal = (String) authenticationToken.getPrincipal();
            String credential = (String) authenticationToken.getCredentials();

            SignInRequestValidationResult signInRequestValidationResult = memberService.validateSignInRequest(new SignInRequest(principal, credential));

            return new UsernamePasswordAuthenticationToken(
                principal, null,
                AuthorityUtils.createAuthorityList(signInRequestValidationResult.getRoleKey())
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
