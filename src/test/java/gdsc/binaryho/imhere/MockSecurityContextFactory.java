package gdsc.binaryho.imhere;

import gdsc.binaryho.imhere.config.auth.PrincipalDetails;
import gdsc.binaryho.imhere.domain.member.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class MockSecurityContextFactory implements WithSecurityContextFactory<MockMember> {

    @Override
    public SecurityContext createSecurityContext(MockMember annotation) {
        Member mockMember = Member.createMember(annotation.univId(), annotation.name(),
            annotation.password(), annotation.role());
        mockMember.setId(annotation.id());

        PrincipalDetails principalDetails = new PrincipalDetails(mockMember);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            principalDetails, "", principalDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(token);
        return context;
    }
}
