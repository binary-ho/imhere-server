package gdsc.binaryho.imhere.core.member.controller;

import gdsc.binaryho.imhere.core.member.application.MemberService;
import gdsc.binaryho.imhere.core.member.model.request.ChangeRoleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin", description = "Admin이 사용할 수 있는 api입니다.")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

    @Operation(summary = "유저 권한변경 API")
    @PostMapping("/role/{univ_id}")
    public ResponseEntity<Void> changeMemberRole(@RequestBody ChangeRoleRequest changeRoleRequest,
        @PathVariable("univ_id") String univId) {
        memberService.changeMemberRole(changeRoleRequest, univId);
        return ResponseEntity.ok().build();
    }
}
