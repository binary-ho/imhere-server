package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.config.auth.PrincipalDetails;
import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.Role;
import org.springframework.security.access.AccessDeniedException;
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
            throw new AccessDeniedException("Access Denied requestId : " + requestId);
        }
    }

    public void verifyMemberHasAdminRole() {
        if (!getCurrentMember().getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Access Denied : User has not role");
        }
    }

    private void validateAuthenticationNotNull(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("No Authentication Member");
        }
    }

    private void validateAuthenticated(Authentication authentication) {
        if (!authentication.isAuthenticated()) {
            throw new AccessDeniedException("UnAuthentication Exception");
        }
    }
}
