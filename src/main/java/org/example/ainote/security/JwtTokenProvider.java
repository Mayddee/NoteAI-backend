package org.example.ainote.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ainote.dto.JwtResponse;
import org.example.ainote.entity.Role;
import org.example.ainote.entity.User;
import org.example.ainote.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
//@Service

@Component
//@RequiredArgsConstructor
public class JwtTokenProvider {
    private final SecretKey secretKey;
    private final UserDetailsService userDetailsService;

    private UserService userService;

    public JwtTokenProvider(@Value("${security.jwt.secret}") String jwtSecret, UserDetailsService userDetailsService, UserService userService){
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    public String createAccessToken(@NonNull User user){
        LocalDateTime now = LocalDateTime.now();
        Instant expirationTime = now.plusHours(12).atZone(ZoneId.systemDefault()).toInstant();
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("roles", resolveRoles(user.getRoles()));
        return Jwts.builder()
                .subject(user.getUsername())
                .claims(claims)
                .setExpiration(Date.from(expirationTime))
                .signWith(secretKey)
                .compact();

    }

    public List<String> resolveRoles(Set<Role> roles){
        return roles.stream().map(Enum::name).collect(Collectors.toList());
    }

    public String createRefreshToken(@NonNull User user){
        LocalDateTime now = LocalDateTime.now();
        Instant expiration = now.plusDays(20).atZone(ZoneId.systemDefault()).toInstant();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("id", user.getId())
                .setExpiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
        Claims body = claims.getBody();
        return body.getExpiration().after(new Date());
    }

    public JwtResponse refresh(String refreshToken){
        JwtResponse jwtResponse = new JwtResponse();
        if(!validateToken(refreshToken)){
            throw new AccessDeniedException("Access denied");
        }
        Long id = Long.valueOf(getId(refreshToken));
        User user = userService.getById(id);
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setId(id);
        jwtResponse.setName(user.getName());
        jwtResponse.setAccessToken(createAccessToken(user));
        jwtResponse.setRefreshToken(createRefreshToken(user));
        return jwtResponse;

    }

    private String getId(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("id")
                .toString();
    }

    private String getUsername(String token){
        return Jwts
                .parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject()
                .toString();
    }

    public Authentication getAuthentication(String token) {
        String username = getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }


}
