package re.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import re.api.models.AppUser;
import re.api.models.UserRole;

import java.security.Key;
import java.util.Date;

@Component
public class JwtConverter {

    private final String ISSUER = "retriever-essentials";
    private final int EXPIRATION_MINUTES = 60;
    private final int EXPIRATION_MILLIS = EXPIRATION_MINUTES * 60 * 1000;

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String getTokenFromUser(AppUser user) {
        return Jwts.builder()
                .setIssuer(ISSUER)
                .setSubject(user.getUsername())
                .claim("appUserId", user.getAppUserId())
                .claim("role", user.getAuthorities().stream()
                        .findFirst().orElse(null).getAuthority()) // should be ROLE_ADMIN or ROLE_AUTHORITY
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MILLIS))
                .signWith(key)
                .compact();
    }

    public AppUser getUserFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }

        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .requireIssuer(ISSUER)
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token.substring(7)); // strip "Bearer "

            Claims claims = jws.getBody();
            int appUserId = (Integer) claims.get("appUserId");
            String email = claims.getSubject();
            String role = (String) claims.get("role");

            return new AppUser(
                    appUserId,
                    email,
                    "[protected]",
                    UserRole.valueOf(role.replace("ROLE_", "")), // convert back from ROLE_ADMIN → ADMIN
                    true
            );
        } catch (JwtException ex) {
            System.out.println("[JWT ERROR] " + ex.getMessage());
        }

        return null;
    }
}
