package gdsc.binaryho.imhere.core.auth.application;

import gdsc.binaryho.imhere.core.auth.PrincipalDetails;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.MemberRepository;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public PrincipalDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String univId) {
        Member member = memberRepository.findByUnivId(univId)
            .orElseThrow(() -> {
                throw new AuthenticationException("There is no such user : " + univId) {};
            });
        return new PrincipalDetails(member);
    }
}
