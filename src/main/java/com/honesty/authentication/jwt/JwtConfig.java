package com.honesty.authentication.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.Date;

@Configuration
@Data
@ConfigurationProperties(prefix = "application.jwt")
public class JwtConfig {

    private String secretKey;
    private String tokenPrefix;
    private Integer tokenExpirationAfterDays;
    private Integer refreshTokenExpirationAfterDays;

    public JwtConfig( ) {

    }


    public String getAccessToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("authorities", userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(getTokenExpirationAfterDays())))
                .signWith(getSecretKey())
                .compact();
    }

    public String getRefreshToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(getRefreshTokenExpirationAfterDays())))
                .signWith(getSecretKey())
                .compact();
    }

    public SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
