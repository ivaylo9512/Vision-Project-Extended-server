package com.vision.project.exceptions;

public class RegistrationIsDisabled extends RuntimeException {
    public RegistrationIsDisabled(String exception) {
        super(exception);
    }
}
