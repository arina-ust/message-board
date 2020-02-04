package home.assignment.messageboard.configuration.jwt;

import home.assignment.messageboard.configuration.ApplicationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    private ApplicationProperties applicationProperties;

    public JwtTokenUtil(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public String getUsernameFromToken(String token) throws ExpiredJwtException {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    private Claims getClaims(String token) throws ExpiredJwtException {
        return Jwts.parser()
                    .setSigningKey(applicationProperties.getSecret())
                    .parseClaimsJws(token)
                    .getBody();
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + applicationProperties.getTokenLifetime() * 1000))
                .signWith(SignatureAlgorithm.HS512, applicationProperties.getSecret())
                .compact();
    }

}
