package gdsc.binaryho.imhere.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Value("${jwt.access-token-prefix}")
    private String SECRET;

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
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact()
        );
    }

    public boolean validateTokenExpirationTime(String token) {
        if (token.isEmpty()) {
            return false;
        }

        try {
            Jws<Claims> claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token);

            return claims.getBody()
                .getExpiration()
                .after(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUnivId(String token) {
        return Jwts.parser()
            .setSigningKey(SECRET)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}
