package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.config.auth.PrincipalDetails;
import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.Role;
import gdsc.binaryho.imhere.exception.member.MemberNotFoundException;
import gdsc.binaryho.imhere.exception.member.PermissionDeniedException;
import gdsc.binaryho.imhere.exception.member.RequestMemberIdMismatchException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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

    public void verifyMemberHasAdminRole() {
        if (!getCurrentMember().getRole().equals(Role.ADMIN)) {
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
