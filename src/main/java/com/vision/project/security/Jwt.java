package com.vision.project.security;

import com.vision.project.models.UserDetails;
import com.vision.project.security.Exceptions.JwtExpiredTokenException;
import com.vision.project.security.Exceptions.JwtTokenIsIncorrectException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Jwt {
    static int jwtExpirationInMs = 10000000;
    static String jwtSecret = "MyJwtSecret";
    static byte[] encodedJwtSecret = Base64.getEncoder().encode(jwtSecret.getBytes());

    static String generate(Authentication auth) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        UserDetails user = (UserDetails) auth.getPrincipal();
        Collection<GrantedAuthority> grantedAuth = user.getAuthorities();
        Set<String> authorities = AuthorityUtils.authorityListToSet(grantedAuth);

        Claims claims = Jwts.claims()
                .setSubject(user.getUsername())
                .setId(String.valueOf(user.getId()));
        claims.put("roles", authorities);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, new String(encodedJwtSecret))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .compact();
    }

    public static UserDetails validate(String token) {
        UserDetails user = null;
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(new String(encodedJwtSecret))
                    .parseClaimsJws(token)
                    .getBody();

            List<SimpleGrantedAuthority> authorities = ((ArrayList<String>)body.get("roles")).stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            user = new UserDetails(body.getSubject(), token, authorities, Integer.parseInt(body.getId()));
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredTokenException("Jwt token has expired.");
        } catch (Exception e) {
            throw new JwtTokenIsIncorrectException("Jwt token is incorrect");
        }
        return user;
    }
}