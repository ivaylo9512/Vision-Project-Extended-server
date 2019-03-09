package com.vision.project.security;
import com.vision.project.models.UserDetails;
import com.vision.project.security.Exceptions.JwtExpiredTokenException;
import com.vision.project.security.Exceptions.JwtTokenIsIncorrectException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Override
    protected void additionalAuthenticationChecks(org.springframework.security.core.userdetails.UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
        String token = usernamePasswordAuthenticationToken.getPrincipal().toString();
        UserDetails userDetails;
            userDetails = Jwt.validate(token);
        usernamePasswordAuthenticationToken.setDetails(userDetails);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        return userDetails;
    }
}