package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.MemberRepository;
import gdsc.binaryho.imhere.domain.member.Role;
import gdsc.binaryho.imhere.mapper.dtos.SignInRequestValidationResult;
import gdsc.binaryho.imhere.mapper.requests.RoleChangeRequest;
import gdsc.binaryho.imhere.mapper.requests.SignInRequest;
import java.security.InvalidParameterException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
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
        log.info("[회원가입] univId : {}, name : {}, role : {}"
            , () -> newMember.getUnivId(), () -> newMember.getName(), () -> newMember.getRoleKey());
        memberRepository.save(newMember);
    }

    private void validateDuplicateMember(String univId) {
        if (memberRepository.findByUnivId(univId).isPresent()) {
            log.info("[회원가입 실패] univId 중복 회원가입 시도 univId : " + univId);
            throw new DuplicateKeyException("Member Already Exist");
        }
    }

    private void validatePasswordForm(String password) {
        if (!password.matches(PASSWORD_REGEX)) {
            throw new IllegalArgumentException();
        }
    }

    @Transactional
    public SignInRequestValidationResult validateSignInRequest(SignInRequest signInRequest) {
        Member member = memberRepository.findByUnivId(signInRequest.getUnivId())
            .orElseThrow(() -> {
                throw new AuthenticationException("There is no such user : " + signInRequest.getUnivId()) {};
            });

        validateMatchesPassword(signInRequest.getPassword(), member.getPassword());

        return new SignInRequestValidationResult(member.getRoleKey());
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

        log.info("[권한 변경] " + univId + "의 권한이 {} 로 변경. ({})",
            () -> roleChangeRequest.getRole(), () -> authenticationHelper.getCurrentMember().getUnivId());
    }
}
