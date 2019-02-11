package com.vision.project.security.Exceptions;

public class JwtTokenIsIncorrectException extends RuntimeException {

    public JwtTokenIsIncorrectException(String exception) {
        super(exception);
    }


}
