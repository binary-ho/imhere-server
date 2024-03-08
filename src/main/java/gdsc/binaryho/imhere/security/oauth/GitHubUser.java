package gdsc.binaryho.imhere.security.oauth;

import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class GitHubUser {

    public static final String GITHUB_NAME_ATTRIBUTE_KEY = "id";
    private static final String GIT_HUB_HANDLE_ATTRIBUTE_NAME = "login";
    private static final String GIT_HUB_AVATAR_URL_ATTRIBUTE_NAME = "avatar_url";

    private final OAuth2User oAuth2User;

    public GitHubUser(OAuth2User oAuth2User) {
        this.oAuth2User = oAuth2User;
    }

    public String getId() {
        return oAuth2User.getName();
    }

    public String getHandle() {
        return oAuth2User.getAttribute(GIT_HUB_HANDLE_ATTRIBUTE_NAME);
    }

    public String getAvatarUrl() {
        return oAuth2User.getAttribute(GIT_HUB_AVATAR_URL_ATTRIBUTE_NAME);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }
}
