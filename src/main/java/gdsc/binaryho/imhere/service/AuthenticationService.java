package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.config.auth.PrincipalDetails;
import gdsc.binaryho.imhere.domain.member.Member;
import java.rmi.NoSuchObjectException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public Member getCurrentMember() throws NoSuchObjectException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            return ((PrincipalDetails) authentication.getPrincipal()).getMember();
        }

        System.out.println("authentication = " + authentication);
        throw new NoSuchObjectException("No Authentication Member");
    }

    public void verifyRequestMemberLogInMember(Long requestId) throws NoSuchObjectException {
        if (!requestId.equals(getCurrentMember().getId())) {
            throw new IllegalArgumentException();
        }
    }

    public void verifyRequestMemberLogInMember(String univId) throws NoSuchObjectException {
        if (!univId.equals(getCurrentMember().getUnivId())) {
            throw new IllegalArgumentException();
        }
    }
}
