package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.mapper.requests.RoleChangeRequest;
import gdsc.binaryho.imhere.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin", description = "Admin이 사용할 수 있는 api입니다.")
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

    @Operation(summary = "유저 권한변경 API")
    @PostMapping("/api/v1/admin/role/{univ_id}")
    public void memberRoleChange(@RequestBody RoleChangeRequest roleChangeRequest,
        @PathVariable("univ_id") String univId) {
        memberService.memberRoleChange(roleChangeRequest, univId);
    }
}
