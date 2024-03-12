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

    private final SeoulDateTimeHolder seoulDateTimeHolder;
    private final TokenPropertyHolder tokenPropertyHolder;

    private static final String ROLE_KEY = "role";

    // TODO : 기존 토큰 생성 방식은 삭제할 예정
    public Token createToken(String univId, String roleKey) {
        Claims claims = Jwts.claims().setSubject(univId);
        return createToken(claims, roleKey);
    }

    public Token createToken(Long memberId, Role role) {
        Claims claims = Jwts.claims().setSubject(memberId.toString());
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

    public Long getId(String token) {
        String tokenSecret = tokenPropertyHolder.getSecret();
        String subject = Jwts.parser()
            .setSigningKey(tokenSecret)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();

        // TODO : Exception Check
        return Long.parseLong(subject);
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
        Duration accessTokenExpiration = tokenPropertyHolder.getAccessTokenExpiration();
        String tokenSecret = tokenPropertyHolder.getSecret();

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date(seoulTimeNow))
            .setExpiration(new Date(seoulTimeNow + accessTokenExpiration.toMillis()))
            .signWith(SignatureAlgorithm.HS256, tokenSecret)
            .compact();
    }

    private void parseToValidateToken(String token) {
        String tokenSecret = tokenPropertyHolder.getSecret();
        Jwts.parser()
            .setSigningKey(tokenSecret)
            .parseClaimsJws(token);
    }
}
