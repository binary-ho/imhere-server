package gdsc.binaryho.imhere.security.util;

import gdsc.binaryho.imhere.core.auth.exception.MemberNotFoundException;
import gdsc.binaryho.imhere.core.auth.exception.PermissionDeniedException;
import gdsc.binaryho.imhere.core.auth.exception.RequestMemberIdMismatchException;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.security.principal.PrincipalDetails;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AuthenticationHelper {

    public Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        validateAuthenticationNotNull(authentication);
        validateAuthenticated(authentication);

        return ((PrincipalDetails) authentication.getPrincipal()).getMember();
    }

    public void verifyRequestMemberLogInMember(Long requestId) {
        if (!requestId.equals(getCurrentMember().getId())) {
            throw RequestMemberIdMismatchException.EXCEPTION;
        }
    }

    public void verifyMemberHasRole(Role role) {
        if (!getCurrentMember().getRole().equals(role)) {
            throw PermissionDeniedException.EXCEPTION;
        }
    }

    private void validateAuthenticationNotNull(Authentication authentication) {
        if (authentication == null) {
            throw MemberNotFoundException.EXCEPTION;
        }
    }

    private void validateAuthenticated(Authentication authentication) {
        if (!authentication.isAuthenticated()) {
            throw MemberNotFoundException.EXCEPTION;
        }
    }
}
