package gdsc.binaryho.imhere.security.oauth;

import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.security.SignUpProcessRedirectionPath;
import lombok.Getter;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final Long memberId;
    private final Role role;
    private final SignUpProcessRedirectionPath signUpProcessRedirectionPath;

    public CustomOAuth2User(GitHubUser gitHubUser, Member member) {
        super(gitHubUser.getAuthorities(), gitHubUser.getAttributes(), GitHubUser.GITHUB_NAME_ATTRIBUTE_KEY);
        this.memberId = member.getId();
        this.role = member.getRole();
        this.signUpProcessRedirectionPath = SignUpProcessRedirectionPath.of(member);
    }
}
