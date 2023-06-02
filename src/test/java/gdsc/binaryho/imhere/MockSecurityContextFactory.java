package gdsc.binaryho.imhere;

import gdsc.binaryho.imhere.core.auth.PrincipalDetails;
import gdsc.binaryho.imhere.core.member.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class MockSecurityContextFactory implements WithSecurityContextFactory<MockSecurityContextMember> {

    @Override
    public SecurityContext createSecurityContext(MockSecurityContextMember annotation) {
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
