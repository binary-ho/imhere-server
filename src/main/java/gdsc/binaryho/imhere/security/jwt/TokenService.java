package gdsc.binaryho.imhere.security.jwt;

import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.util.SeoulDateTimeHolder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Duration;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class TokenService {

    private final SecretHolder secretHolder;
    private final SeoulDateTimeHolder seoulDateTimeHolder;

    private static final String ROLE_KEY = "role";
    private static final Duration ACCESS_TOKEN_EXPIRATION_TIME = Duration.ofMinutes(30);

    // TODO : 기존 토큰 생성 방식은 삭제할 예정
    public Token createToken(String univId, String roleKey) {
        Claims claims = Jwts.claims().setSubject(univId);
        return createToken(claims, roleKey);
    }

    public Token createToken(Long id, Role role) {
        Claims claims = Jwts.claims().setSubject(id.toString());
        return createToken(claims, role.getKey());
    }

    public boolean validateTokenExpirationTimeNotExpired(String token) {
        if (isNullOrEmpty(token)) {
            return false;
        }

        try {
            parseToValidateToken(token);
            return true;
        } catch (ExpiredJwtException exception) {
            return false;
        } catch (JwtException | IllegalArgumentException exception) {
            log.info("[토큰 에러] {}", exception::getMessage);
            return false;
        }
    }

    public String getUnivId(String token) {
        return Jwts.parser()
            .setSigningKey(secretHolder.getSecret())
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    private Token createToken(Claims claims, String roleKey) {
        claims.put(ROLE_KEY, roleKey);

        String jwtToken = buildJwtToken(claims);
        return new Token(jwtToken);
    }

    private Boolean isNullOrEmpty(String token) {
        return token == null || token.isEmpty();
    }

    private String buildJwtToken(Claims claims) {
        long seoulTimeNow = seoulDateTimeHolder.getSeoulMilliseconds();
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date(seoulTimeNow))
            .setExpiration(new Date(seoulTimeNow + ACCESS_TOKEN_EXPIRATION_TIME.toMillis()))
            .signWith(SignatureAlgorithm.HS256, secretHolder.getSecret())
            .compact();
    }

    private void parseToValidateToken(String token) {
        Jwts.parser()
            .setSigningKey(secretHolder.getSecret())
            .parseClaimsJws(token);
    }
}
