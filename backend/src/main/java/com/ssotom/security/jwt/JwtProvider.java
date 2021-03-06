package com.ssotom.security.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    private final static String JWT_SECRET = "Aji6KfBv6ktosBxmvQZjfsuaazJ3WBhLsMGtMGVg7LJumdW";
    
    public final static long JWT_EXPIRATION = 24*360000L; //HOURS*milliseconds
    
    public final static long JWT_EXPIRATION_LONG = 180*86400000L; //DAYS*milliseconds
    

    public String generateJwtToken(Authentication authentication) {

        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
        				.setSubject((userPrincipal.getUsername()))
		                .setIssuedAt(new Date())
		                .setExpiration(new Date((new Date()).getTime() + JWT_EXPIRATION))
		                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
		                .compact();
    }
    
    public String generateJwtToken(String username) {
        return Jwts.builder()
        				.setSubject(username)
		                .setIssuedAt(new Date())
		                .setExpiration(new Date((new Date()).getTime() + JWT_EXPIRATION_LONG))
		                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
		                .compact();
    }
    
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature -> Message: {} ", e);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token -> Message: {}", e);
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token -> Message: {}", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token -> Message: {}", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty -> Message: {}", e);
        }
        
        return false;
    }
    
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
        				.setSigningKey(JWT_SECRET)
			            .parseClaimsJws(token)
			            .getBody().getSubject();
    }
    
}
