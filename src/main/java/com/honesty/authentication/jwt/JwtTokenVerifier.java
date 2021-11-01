package com.honesty.authentication.jwt;

import com.honesty.authentication.user.ApplicationUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class JwtTokenVerifier extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final ApplicationUserService applicationUserService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authorizationHeader == null || !authorizationHeader.startsWith(jwtConfig.getTokenPrefix())){
            filterChain.doFilter(request, response);
            return;
        }
        try {


            String token = authorizationHeader.replace(jwtConfig.getTokenPrefix(),"");
            verifyAccessToken(request, token);

        }catch (JwtException e){
            String refreshToken = request.getHeader("refreshToken");
            if(refreshToken == null){
                filterChain.doFilter(request, response);
                return;
            }

            String username = verifyRefreshToken(refreshToken);
            if(username != null){
                UserDetails userDetails = applicationUserService.getUserDetailsById(username);
                String token = jwtConfig.getAccessToken(userDetails);
                response.addHeader("access-token",token );
                response.addHeader("refresh-token", jwtConfig.getRefreshToken(userDetails));

                verifyAccessToken(request, token);
            }else {
                System.err.println("token cannot be trusted");
            }

        }
        filterChain.doFilter(request, response);
    }



    public void verifyAccessToken(HttpServletRequest request, String token){
        Jws<Claims> claimsJws =  Jwts.parser().setSigningKey(jwtConfig.getSecretKey()).parseClaimsJws(token);

        Claims body = claimsJws.getBody();
        String username = body.getSubject();
        var authorities = (List<Map<String, String>>) body.get("authorities");
        Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                .map(m -> new SimpleGrantedAuthority(m.get("authority")))
                .collect(Collectors.toSet());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                simpleGrantedAuthorities
        );
        request.setAttribute("username", username);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public String verifyRefreshToken(String refreshToken) {
        try {
            Jws<Claims> claimsJws =  Jwts.parser().setSigningKey(jwtConfig.getSecretKey()).parseClaimsJws(refreshToken);
            Claims body = claimsJws.getBody();
            String username = body.getSubject();
            return username;
        }catch (JwtException e){
            return null;
        }

    }
}
