package gdsc.binaryho.imhere.security.oauth;

import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        GitHubUser gitHubUser = new GitHubUser(super.loadUser(userRequest));

        Member member = memberRepository.findByGitHubResource_Id(gitHubUser.getId())
            .orElseGet(() -> {
                Member guest = createGuestMember(gitHubUser);
                return memberRepository.save(guest);
            });

        updateGitHubHandleIfChanged(member, gitHubUser.getHandle());
        return new CustomOAuth2User(gitHubUser, member);
    }

    private static Member createGuestMember(GitHubUser gitHubUser) {
        return Member.createGuestMember(
            gitHubUser.getId(), gitHubUser.getHandle(), gitHubUser.getAvatarUrl());
    }

    private void updateGitHubHandleIfChanged(Member member, String gitHubHandle) {
        if (!member.getGitHubResource().getHandle().equals(gitHubHandle)) {
            member.updateGitHubHandle(gitHubHandle);
        }
    }
}
