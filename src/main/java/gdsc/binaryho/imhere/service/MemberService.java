package gdsc.binaryho.imhere.service;

import com.sun.jdi.request.DuplicateRequestException;
import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.MemberRepository;
import gdsc.binaryho.imhere.domain.member.Role;
import gdsc.binaryho.imhere.mapper.dtos.SignInResponseDto;
import gdsc.binaryho.imhere.mapper.requests.RoleChangeRequest;
import gdsc.binaryho.imhere.mapper.requests.SignInRequest;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{8,20}$";

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;

    @Transactional
    public void signUp(String univId, String name, String password) {
        validateDuplicateMember(univId);
        validatePasswordForm(password);
        Member newMember = Member.createMember(univId, name, bCryptPasswordEncoder.encode(password), Role.STUDENT);
        memberRepository.save(newMember);
    }

    private void validateDuplicateMember(String univId) {
        if (memberRepository.findByUnivId(univId).isPresent()) {
            throw new DuplicateRequestException();
        }
    }

    private void validatePasswordForm(String password) {
        if (!password.matches(PASSWORD_REGEX)) {
            throw new IllegalArgumentException();
        }
    }

    @Transactional
    public SignInResponseDto login(SignInRequest signInRequest) {
        Member member = memberRepository.findByUnivId(signInRequest.getUnivId()).orElseThrow();

        if (validateMatchesPassword(signInRequest.getPassword(), member.getPassword())) {
            return new SignInResponseDto(member.getUnivId(), member.getRoleKey());
        }

        throw new IllegalArgumentException();
    }

    private boolean validateMatchesPassword(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }

    @Transactional
    public void memberRoleChange(RoleChangeRequest roleChangeRequest, Long memberId) {
        Member targetMember = memberRepository.findById(memberId).orElseThrow();

        Role newRole = Role.valueOf(roleChangeRequest.getRole());
        targetMember.setRole(newRole);
    }
}
