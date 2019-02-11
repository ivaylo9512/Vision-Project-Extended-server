package com.vision.project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Component
public class Jwt {
    static int jwtExpirationInMs = 10000000;
    static String jwtSecret = "MyJwtSecret";

    static String generate(Authentication auth) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        byte[] encodedBytes = Base64.getEncoder().encode(jwtSecret.getBytes());

        User user = (User) auth.getPrincipal();
        Collection<GrantedAuthority> grantedAuth = user.getAuthorities();
        Set<String> authorities = AuthorityUtils.authorityListToSet(grantedAuth);

        Claims claims = Jwts.claims()
                .setSubject(user.getUsername());
        claims.put("roles", authorities);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, new String(encodedBytes))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .compact();
    }
}