package gdsc.binaryho.imhere.security.jwt;

import static gdsc.binaryho.imhere.fixture.MemberFixture.ROLE;
import static gdsc.binaryho.imhere.fixture.MemberFixture.UNIV_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gdsc.binaryho.imhere.mock.FakeSecretHolder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

public class TokenServiceTest {

    private static final String SECRET = "TEST_SECRET";

    SecretHolder secretHolder = new FakeSecretHolder(SECRET);
    TokenService tokenService = new TokenService(secretHolder);

    @Test
    void 이메일과_권한을_넣어_토큰을_만들_수_있다() {
        Token token = tokenService.createToken(UNIV_ID, ROLE.getKey());
        String accessToken = token.getAccessToken();

        Claims claims = Jwts.parser()
            .setSigningKey(secretHolder.getSecret())
            .parseClaimsJws(accessToken)
            .getBody();

        assertAll(
            () -> assertThat(claims.getSubject()).isEqualTo(UNIV_ID),
            () -> assertThat(claims.get("role")).isEqualTo(ROLE.getKey())
        );
    }

    @Test
    void 토큰에서_UnivId를_추출할_수_있다() {
        Token token = tokenService.createToken(UNIV_ID, ROLE.getKey());
        String accessToken = token.getAccessToken();

        String parsedUnivId = tokenService.getUnivId(accessToken);

        assertThat(parsedUnivId).isEqualTo(UNIV_ID);
    }
}
