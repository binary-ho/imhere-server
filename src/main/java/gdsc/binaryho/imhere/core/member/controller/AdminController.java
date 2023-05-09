package gdsc.binaryho.imhere.core.member.controller;

import gdsc.binaryho.imhere.core.auth.application.AuthService;
import gdsc.binaryho.imhere.core.member.application.request.RoleChangeRequest;
import gdsc.binaryho.imhere.exception.ImhereException;
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
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;

    @Operation(summary = "유저 권한변경 API")
    @PostMapping("/role/{univ_id}")
    public ResponseEntity<Void> memberRoleChange(@RequestBody RoleChangeRequest roleChangeRequest,
        @PathVariable("univ_id") String univId) {
        try {
            authService.memberRoleChange(roleChangeRequest, univId);
            return ResponseEntity.ok().build();
        } catch (ImhereException error) {
            return ResponseEntity
                .status(error.getErrorInfo().getHttpStatus())
                .build();
        }
    }
}
