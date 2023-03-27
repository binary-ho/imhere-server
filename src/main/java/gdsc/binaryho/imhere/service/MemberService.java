package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.MemberRepository;
import gdsc.binaryho.imhere.domain.member.Role;
import gdsc.binaryho.imhere.mapper.dtos.SignInResponseDto;
import gdsc.binaryho.imhere.mapper.requests.RoleChangeRequest;
import gdsc.binaryho.imhere.mapper.requests.SignInRequest;
import java.security.InvalidParameterException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final AuthenticationHelper authenticationHelper;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{8,20}$";

    @Transactional
    public void signUp(String univId, String name, String password) {
        validateDuplicateMember(univId);
        validatePasswordForm(password);

        Member newMember = Member.createMember(univId, name, bCryptPasswordEncoder.encode(password), Role.STUDENT);
        System.out.println("newMember = " + newMember.getName() + ", " + newMember.getUnivId());
        memberRepository.save(newMember);
    }

    private void validateDuplicateMember(String univId) {
        if (memberRepository.findByUnivId(univId).isPresent()) {
            throw new DuplicateKeyException("Member Already Exist");
        }
    }

    private void validatePasswordForm(String password) {
        if (!password.matches(PASSWORD_REGEX)) {
            throw new IllegalArgumentException();
        }
    }

    @Transactional
    public SignInResponseDto login(SignInRequest signInRequest) {
        Member member = memberRepository.findByUnivId(signInRequest.getUnivId())
            .orElseThrow(() -> {
                throw new AuthenticationException("There is no such user : " + signInRequest.getUnivId()) {};
            });

        validateMatchesPassword(signInRequest.getPassword(), member.getPassword());

        return new SignInResponseDto(member.getUnivId(), member.getRoleKey());
    }

    private void validateMatchesPassword(String rawPassword, String encodedPassword) {
        if (!bCryptPasswordEncoder.matches(rawPassword, encodedPassword)) {
            throw new InvalidParameterException();
        }
    }

    @Transactional
    public void memberRoleChange(RoleChangeRequest roleChangeRequest, String univId) {
        authenticationHelper.verifyMemberHasAdminRole();

        Member targetMember = memberRepository.findByUnivId(univId)
            .orElseThrow(EntityNotFoundException::new);

        Role newRole = Role.valueOf(roleChangeRequest.getRole());
        targetMember.setRole(newRole);
    }
}
