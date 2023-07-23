package gdsc.binaryho.imhere.core.member.application;

import gdsc.binaryho.imhere.core.auth.exception.MemberNotFoundException;
import gdsc.binaryho.imhere.core.auth.util.AuthenticationHelper;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.MemberRepository;
import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.core.member.model.request.RoleChangeRequest;
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
