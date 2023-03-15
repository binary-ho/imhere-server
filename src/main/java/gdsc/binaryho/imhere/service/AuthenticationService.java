package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.config.auth.PrincipalDetails;
import gdsc.binaryho.imhere.domain.member.Member;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public static Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException("No Authentication Member");
        }

        if (!authentication.isAuthenticated()) {
            throw new AccessDeniedException("UnAuthentication Exception");
        }

        return ((PrincipalDetails) authentication.getPrincipal()).getMember();
    }

    public static void verifyRequestMemberLogInMember(Long requestId) {
        if (!requestId.equals(getCurrentMember().getId())) {
            throw new AccessDeniedException("Access Denied requestId : " + requestId);
        }
    }

    public static void verifyRequestMemberLogInMember(String univId)  {
        if (!univId.equals(getCurrentMember().getUnivId())) {
            throw new AccessDeniedException("Access Denied univId : " + univId);
        }
    }
}
