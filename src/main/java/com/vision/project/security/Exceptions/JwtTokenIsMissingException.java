package com.vision.project.security.Exceptions;

public class JwtTokenIsMissingException extends RuntimeException {


    public JwtTokenIsMissingException(String exception) {
        super(exception);
    }

}
