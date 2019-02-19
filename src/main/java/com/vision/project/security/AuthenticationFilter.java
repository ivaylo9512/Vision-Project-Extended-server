package com.vision.project.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.project.models.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private byte[] encodedBytes = Base64.getEncoder().encode(Jwt.jwtSecret.getBytes());

    public AuthenticationFilter() {
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws BadCredentialsException {
        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);

            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),
                    user.getPassword(), new ArrayList<>()));

        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication auth) throws IOException, ServletException {

        String token = Jwt.generate(auth);
        response.addHeader("Authorization", "Token " + token);
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        response.getWriter().write("Authenticated");
    }
}
