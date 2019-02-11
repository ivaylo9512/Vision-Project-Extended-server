package com.vision.project.security.Exceptions;

public class JwtExpiredTokenException extends RuntimeException{

    public JwtExpiredTokenException(String exception) {
        super(exception);
    }
}
