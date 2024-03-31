package gdsc.binaryho.imhere.core.member.application;

import gdsc.binaryho.imhere.core.auth.exception.MemberNotFoundException;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import gdsc.binaryho.imhere.core.member.model.request.ChangeRoleRequest;
import gdsc.binaryho.imhere.security.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberService {

    private final AuthenticationHelper authenticationHelper;
    private final MemberRepository memberRepository;

    @Transactional
    public void changeMemberRole(ChangeRoleRequest changeRoleRequest, String univId) {
        authenticationHelper.verifyMemberHasRole(Role.ADMIN);

        Member targetMember = memberRepository.findByUnivId(univId)
            .orElseThrow(() -> MemberNotFoundException.EXCEPTION);

        Role newRole = Role.valueOf(changeRoleRequest.getRole());
        targetMember.setRole(newRole);

        Member admin = authenticationHelper.getCurrentMember();
        log.info("[권한 변경] " + univId + "의 권한이 {} 로 변경. 변경자 : ({})",
            changeRoleRequest::getRole, admin::getUnivId);
    }
}
