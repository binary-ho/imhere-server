package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.MemberRepository;
import gdsc.binaryho.imhere.domain.member.Role;
import gdsc.binaryho.imhere.exception.member.EmailDuplicatedException;
import gdsc.binaryho.imhere.exception.member.MemberNotFoundException;
import gdsc.binaryho.imhere.exception.member.PasswordFormatMismatchException;
import gdsc.binaryho.imhere.exception.member.PasswordIncorrectException;
import gdsc.binaryho.imhere.mapper.dtos.SignInRequestValidationResult;
import gdsc.binaryho.imhere.mapper.requests.RoleChangeRequest;
import gdsc.binaryho.imhere.mapper.requests.SignInRequest;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
            throw EmailDuplicatedException.EXCEPTION;
        }
    }

    private void validatePasswordForm(String password) {
        if (!password.matches(PASSWORD_REGEX)) {
            throw PasswordFormatMismatchException.EXCEPTION;
        }
    }

    @Transactional
    public SignInRequestValidationResult validateSignInRequest(SignInRequest signInRequest) {
        // 분리합시다.
        Member member = memberRepository.findByUnivId(signInRequest.getUnivId())
            .orElseThrow(() -> MemberNotFoundException.EXCEPTION);

        validateMatchesPassword(signInRequest.getPassword(), member.getPassword());

        return new SignInRequestValidationResult(member.getRoleKey());
    }

    private void validateMatchesPassword(String rawPassword, String encodedPassword) {
        if (!bCryptPasswordEncoder.matches(rawPassword, encodedPassword)) {
            throw PasswordIncorrectException.EXCEPTION;
        }
    }

    @Transactional
    public void memberRoleChange(RoleChangeRequest roleChangeRequest, String univId) {
        authenticationHelper.verifyMemberHasAdminRole();

        Member targetMember = memberRepository.findByUnivId(univId)
            .orElseThrow(() -> MemberNotFoundException.EXCEPTION);

        Role newRole = Role.valueOf(roleChangeRequest.getRole());
        targetMember.setRole(newRole);

        log.info("[권한 변경] " + univId + "의 권한이 {} 로 변경. ({})",
            () -> roleChangeRequest.getRole(), () -> authenticationHelper.getCurrentMember().getUnivId());
    }
}
