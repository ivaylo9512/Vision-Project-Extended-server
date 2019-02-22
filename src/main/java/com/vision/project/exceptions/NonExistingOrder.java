package com.vision.project.exceptions;

public class NonExistingOrder extends RuntimeException {
    public NonExistingOrder(String exception) {
        super(exception);
    }
}
