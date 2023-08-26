package gdsc.binaryho.imhere.security.jwt;

import gdsc.binaryho.imhere.util.SeoulDateTime;
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
    private static final Duration ACCESS_TOKEN_EXPIRATION_TIME = Duration.ofMinutes(30);

    public Token createToken(String univId, String roleKey) {
        Claims claims = Jwts.claims().setSubject(univId);
        claims.put("role", roleKey);

        long seoulTimeNow = SeoulDateTime.getMillisecondsNow();

        String jwt = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date(seoulTimeNow))
            .setExpiration(new Date(seoulTimeNow + ACCESS_TOKEN_EXPIRATION_TIME.toMillis()))
            .signWith(SignatureAlgorithm.HS256, secretHolder.getSecret())
            .compact();

        return new Token(jwt);
    }

    public boolean validateTokenExpirationTimeNotExpired(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            Jwts.parser()
                .setSigningKey(secretHolder.getSecret())
                .parseClaimsJws(token);
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
}
