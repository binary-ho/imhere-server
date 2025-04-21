package gdsc.binaryho.imhere.core.member;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GitHubResource {

    @Column(unique = true, name = "git_hub_id", nullable = false)
    private String id;

    @Column(name = "git_hub_handle", nullable = false)
    private String handle;

    @Column(name = "git_hub_profile")
    private String profile;

    protected void updateHandle(String gitHubHandle) {
        this.handle = gitHubHandle;
    }
}
