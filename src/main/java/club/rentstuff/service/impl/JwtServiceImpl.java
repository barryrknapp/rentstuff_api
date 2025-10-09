package club.rentstuff.service.impl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import club.rentstuff.entity.UserEntity;
import club.rentstuff.service.ConfigService;
import club.rentstuff.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {

    private static final long EXPIRATION_HOURS = 24;

    @Autowired
    private ConfigService configService;

    @Override
    public String generateToken(UserEntity user) {
    	
    	SecretKey key = Jwts.SIG.HS512.key().build(); // or .generate(); check your JJWT version
    	String base64 = Base64.getEncoder().encodeToString(key.getEncoded());
    	System.out.println("Calculate JWT Key:" + base64);
    	
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
            return false;
        }
    }

    private SecretKey getSigningKey() {  // ✅ changed Key -> SecretKey
        String secret = configService.getConfig("JWT_SECRET");
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT_SECRET must be at least 32 characters long");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
