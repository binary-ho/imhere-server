package gdsc.binaryho.imhere.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class TokenService {

    private final SecretHolder secretHolder;

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000L * 60L * 20L;

    public Token createToken(String univId, String roleKey) {
        Claims memberClaims = Jwts.claims().setSubject(univId);
        memberClaims.put("role", roleKey);

        Date timeNow = new Date();
        return new Token(
            Jwts.builder()
                .setClaims(memberClaims)
                .setIssuedAt(timeNow)
                .setExpiration(new Date(timeNow.getTime() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, secretHolder.getSecret())
                .compact()
        );
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
            log.info("[토큰 에러] {}", () -> exception.getMessage());
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
