package gifterz.textme.global.security.jwt;

import gifterz.textme.domain.user.entity.User;
import gifterz.textme.global.auth.role.UserRole;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${security.jwt.token.secretKey}")
    private String jwtSecret;

    @Value("${security.jwt.token.expirationMs}")
    private Long jwtExpirationMs;


    public String generateAccessToken(User user) {
        return generateToken(user, jwtExpirationMs);
    }

    private String generateToken(User user, Long expirationMs) {
        Claims claims = Jwts.claims();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getUserRole());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public JwtAuthentication getAuthentication(String authToken) {
        Claims claims = parseClaims(authToken);
        Long userId = claims.get("userId", Long.class);
        String email = claims.get("email", String.class);
        String userRole = claims.get("role", String.class);
        return new JwtAuthentication(userId, email, UserRole.valueOf(userRole));

    }

    public Long getUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    public String getEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    public String getUserRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(jwtSecret)
                    .parseClaimsJws(token).getBody();
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return null;
    }
}
