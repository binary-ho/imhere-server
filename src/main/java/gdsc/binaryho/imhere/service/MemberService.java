package gdsc.binaryho.imhere.service;

import com.sun.jdi.request.DuplicateRequestException;
import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.MemberRepository;
import gdsc.binaryho.imhere.domain.member.Role;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;


    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void signUp(String univId, String name, String password) {
        validateDuplicate(univId);
        Member newMember = Member.createMember(univId, name, password, Role.STUDENT);
        memberRepository.save(newMember);
    }

    private void validateDuplicate(String univId) {
        if (memberRepository.findByUnivId(univId).isPresent()) {
            throw new DuplicateRequestException();
        }
    }
}
