package gdsc.binaryho.imhere.core.auth.application;

import gdsc.binaryho.imhere.core.auth.exception.DuplicateEmailException;
import gdsc.binaryho.imhere.core.auth.exception.MemberNotFoundException;
import gdsc.binaryho.imhere.core.auth.exception.PasswordChangeMemberNotExistException;
import gdsc.binaryho.imhere.core.auth.exception.PasswordFormatMismatchException;
import gdsc.binaryho.imhere.core.auth.exception.PasswordIncorrectException;
import gdsc.binaryho.imhere.core.auth.exception.PasswordNullException;
import gdsc.binaryho.imhere.core.auth.exception.PasswordsNotEqualException;
import gdsc.binaryho.imhere.core.auth.model.request.ChangePasswordRequest;
import gdsc.binaryho.imhere.core.auth.model.request.SendPasswordChangeEmailRequest;
import gdsc.binaryho.imhere.core.auth.model.request.SendSignUpEmailRequest;
import gdsc.binaryho.imhere.core.auth.model.request.SignInRequest;
import gdsc.binaryho.imhere.core.auth.model.response.SignInRequestValidationResult;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailVerificationService emailVerificationService;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{8,20}$";

    @Transactional
    public void signUp(String univId, String name, String password) {
        validateMemberNotExist(univId);
        validatePasswordForm(password);

        Member newMember = Member.createMember(univId, name, bCryptPasswordEncoder.encode(password), Role.STUDENT);
        log.info("[회원가입] univId : {}, name : {}, role : {}"
            , newMember::getUnivId, newMember::getName, newMember::getRoleKey);
        memberRepository.save(newMember);
    }

    @Transactional(readOnly = true)
    public SignInRequestValidationResult validateSignInRequest(SignInRequest signInRequest) {
        Member member = memberRepository.findByUnivId(signInRequest.getUnivId())
            .orElseThrow(() -> MemberNotFoundException.EXCEPTION);

        validateMatchesPassword(signInRequest.getPassword(), member.getPassword());

        return new SignInRequestValidationResult(member.getRoleKey());
    }

    @Transactional
    public void sendSignUpEmail(SendSignUpEmailRequest request) {
        validateMemberNotExist(request.getEmail());
        emailVerificationService.sendVerificationCodeByEmail(request.getEmail());
    }

    @Transactional
    public void sendPasswordChangeEmail(SendPasswordChangeEmailRequest request) {
        validateMemberExist(request.getEmail());
        emailVerificationService.sendVerificationCodeByEmail(request.getEmail());
    }

    @Transactional
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        String email = changePasswordRequest.getEmail();
        emailVerificationService.verifyCode(email, changePasswordRequest.getVerificationCode());

        String newPassword = changePasswordRequest.getNewPassword();
        validatePasswords(newPassword, changePasswordRequest.getConfirmationPassword());

        Member member = memberRepository.findByUnivId(changePasswordRequest.getEmail())
            .orElseThrow(() -> PasswordChangeMemberNotExistException.EXCEPTION);
        member.setPassword(bCryptPasswordEncoder.encode(newPassword));
    }

    private void validateMemberNotExist(String email) {
        if (memberRepository.findByUnivId(email).isPresent()) {
            log.info("[회원가입 실패] 중복 이메일 회원가입 시도 -> univId : " + email);
            throw DuplicateEmailException.EXCEPTION;
        }
    }

    private void validateMemberExist(String email) {
        if (memberRepository.findByUnivId(email).isEmpty()) {
            log.info(
                "[비밀번호 변경 시도 실패] 가입하지 않은 회원이 비밀번호 변경 요청 -> email : {}" + email);
            throw PasswordChangeMemberNotExistException.EXCEPTION;
        }
    }

    private void validatePasswordForm(String password) {
        if (!password.matches(PASSWORD_REGEX)) {
            throw PasswordFormatMismatchException.EXCEPTION;
        }
    }

    private void validateMatchesPassword(String rawPassword, String encodedPassword) {
        if (!bCryptPasswordEncoder.matches(rawPassword, encodedPassword)) {
            throw PasswordIncorrectException.EXCEPTION;
        }
    }

    private void validatePasswords(String newPassword, String confirmationPassword) {
        if (newPassword == null || confirmationPassword == null) {
            throw PasswordNullException.EXCEPTION;
        }

        validateRequestPasswordsAreEqual(newPassword, confirmationPassword);
        validatePasswordForm(newPassword);
    }

    private void validateRequestPasswordsAreEqual(String newPassword, String confirmationPassword) {
        if (!Objects.equals(newPassword, confirmationPassword)) {
            throw PasswordsNotEqualException.EXCEPTION;
        }
    }
}
