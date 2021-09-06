package com.vision.project.exceptions;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(String exception) {
        super(exception);
    }
}
