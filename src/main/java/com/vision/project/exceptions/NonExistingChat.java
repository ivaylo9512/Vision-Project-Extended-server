package com.vision.project.exceptions;

public class NonExistingChat extends RuntimeException {
    public NonExistingChat(String exception) {
        super(exception);
    }
}
