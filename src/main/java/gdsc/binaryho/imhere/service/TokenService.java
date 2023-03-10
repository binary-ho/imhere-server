package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.config.jwt.Token;
import gdsc.binaryho.imhere.domain.member.Role;
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
//    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000L * 60L * 60L * 24L * 8L;

    public Token createToken(Long memberId, Role role) {
        Claims memberClaims = Jwts.claims().setSubject(String.valueOf(memberId));
        memberClaims.put("role", role);

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

    public long getMemberId(String token) {
        String memberId = Jwts.parser()
            .setSigningKey(SECRET)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();

        return Long.parseLong(memberId);
    }
}
