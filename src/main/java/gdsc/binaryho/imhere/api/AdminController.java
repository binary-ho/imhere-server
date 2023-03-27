package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.mapper.requests.RoleChangeRequest;
import gdsc.binaryho.imhere.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

    @PostMapping("/api/v1/admin/role/{univ_id}")
    public void memberRoleChange(@RequestBody RoleChangeRequest roleChangeRequest,
        @PathVariable("univ_id") String univId) {
        memberService.memberRoleChange(roleChangeRequest, univId);
    }
}
