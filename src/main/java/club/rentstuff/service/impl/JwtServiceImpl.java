package club.rentstuff.service.impl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import club.rentstuff.entity.UserEntity;
import club.rentstuff.service.ConfigService;
import club.rentstuff.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class JwtServiceImpl implements JwtService {

    private static final long EXPIRATION_HOURS = 24;

    @Autowired
    private ConfigService configService;
    
	@Value("${rentstuff.jwt.secret}")
	private String jwtSecret;

    @Override
    public String generateToken(UserEntity user) {
    	
    	
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(Date.from(
                        LocalDateTime.now()
                                .plusHours(EXPIRATION_HOURS)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                ))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    @Override
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())   // ✅ now accepts SecretKey
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
        	log.error(e);
            return false;
        }
    }

    private SecretKey getSigningKey() {  // ✅ changed Key -> SecretKey
        String secret = jwtSecret;
        log.info("JWT " + secret);
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT_SECRET must be at least 32 characters long");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
