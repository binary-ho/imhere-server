package gdsc.binaryho.imhere.service;

import gdsc.binaryho.imhere.config.jwt.Token;
import gdsc.binaryho.imhere.domain.member.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JWTService {

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
}
