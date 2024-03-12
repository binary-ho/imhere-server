package gdsc.binaryho.imhere.security.jwt;

import static gdsc.binaryho.imhere.mock.fixture.MemberFixture.MOCK_STUDENT;
import static gdsc.binaryho.imhere.mock.fixture.MemberFixture.UNIV_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.mock.FixedSeoulTimeHolder;
import gdsc.binaryho.imhere.mock.FakeTokenPropertyHolder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class TokenServiceTest {

    private static final Role ROLE = MOCK_STUDENT.getRole();
    private static final String SECRET = "TEST_SECRET";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000L * 60L * 20L;
    private static final long TIME_NOW = FixedSeoulTimeHolder.FIXED_MILLISECONDS;

    TokenPropertyHolder tokenPropertyHolder = new FakeTokenPropertyHolder(SECRET, Duration.ofDays(999L));
    TokenService tokenService = new TokenService(new FixedSeoulTimeHolder(), tokenPropertyHolder);

    @Test
    void 이메일과_권한을_넣어_토큰을_만들_수_있다() throws JsonProcessingException {
        Token token = tokenService.createToken(UNIV_ID, ROLE.getKey());
        String accessToken = token.getAccessToken();

        String[] splitToken = accessToken.split("\\.");
        String payload = new String(Base64.getDecoder().decode(splitToken[1]));
        System.out.println(payload);
        JsonNode payloadJson = new ObjectMapper().readTree(payload);
        String subject = payloadJson.get("sub").asText();
        String role = payloadJson.get("role").asText();

        assertAll(
            () -> assertThat(subject).isEqualTo(UNIV_ID),
            () -> assertThat(role).isEqualTo(ROLE.getKey())
        );
    }

    @Test
    void 맴버_아이디와_권한을_넣어_토큰을_만들_수_있다() throws JsonProcessingException {
        Long id = MOCK_STUDENT.getId();
        Token token = tokenService.createToken(id, ROLE);
        String accessToken = token.getAccessToken();

        String[] splitToken = accessToken.split("\\.");
        String payload = new String(Base64.getDecoder().decode(splitToken[1]));
        System.out.println(payload);
        JsonNode payloadJson = new ObjectMapper().readTree(payload);
        Long subject = payloadJson.get("sub").asLong();
        String role = payloadJson.get("role").asText();

        assertAll(
            () -> assertThat(subject).isEqualTo(id),
            () -> assertThat(role).isEqualTo(ROLE.getKey())
        );
    }

    @Test
    void 토큰의_유효_시간의_만료_여부를_확인할_수_있다() {
        // given
        Claims claims = Jwts.claims().setSubject(UNIV_ID);

        String jwt = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date(TIME_NOW))
            .setExpiration(
                new Date(TIME_NOW + ACCESS_TOKEN_EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS256, tokenPropertyHolder.getSecret())
            .compact();
        Token token = new Token(jwt);

        // when
        // then
        assertThat(
            tokenService.validateTokenExpirationTimeNotExpired(token.getAccessToken()))
            .isNotNull();
    }

    @Test
    void 토큰의_유효_시간이_만료되지_않은_경우_true를_반환한다() {
        // given
        Claims claims = Jwts.claims().setSubject(UNIV_ID);

        String jwt = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date(TIME_NOW))
            .setExpiration(
                new Date(TIME_NOW + ACCESS_TOKEN_EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS256, tokenPropertyHolder.getSecret())
            .compact();
        Token token = new Token(jwt);

        // when
        // then
        assertThat(
            tokenService.validateTokenExpirationTimeNotExpired(token.getAccessToken()))
            .isTrue();
    }

    @Test
    void 토큰의_유효_시간이_만료된_경우_false를_반환한다() {
        // given
        Claims claims = Jwts.claims().setSubject(UNIV_ID);

        String jwt = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date(TIME_NOW))
            .setExpiration(
                new Date(TIME_NOW - Duration.ofDays(7777L).toMillis()))
            .signWith(SignatureAlgorithm.HS256, tokenPropertyHolder.getSecret())
            .compact();
        Token token = new Token(jwt);

        // when
        // then
        assertThat(
            tokenService.validateTokenExpirationTimeNotExpired(token.getAccessToken()))
            .isFalse();
    }

    @Test
    void 토큰의_유효시간_만료를_확인할_떄_토큰이_null인_경우_false를_반환한다() {
        // given
        Token token = new Token(null);

        // when
        // then
        assertThat(
            tokenService.validateTokenExpirationTimeNotExpired(token.getAccessToken()))
            .isFalse();
    }

    @Test
    void 토큰의_유효시간_만료를_확인할_떄_토큰이_빈_문자열인_경우_false를_반환한다() {
        // given
        Token token = new Token("");

        // when
        // then
        assertThat(
            tokenService.validateTokenExpirationTimeNotExpired(token.getAccessToken()))
            .isFalse();
    }

    @Test
    void 토큰의_유효시간_만료를_확인할_떄_토큰이_유효하지_않은_문자열인_경우_false를_반환한다() {
        // given
        Token token = new Token(UUID.randomUUID().toString());

        // when
        // then
        assertThat(
            tokenService.validateTokenExpirationTimeNotExpired(token.getAccessToken()))
            .isFalse();
    }
}
