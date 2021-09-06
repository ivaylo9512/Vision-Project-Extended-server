package com.vision.project.exceptions;

public class DisabledUserException extends RuntimeException{
    public DisabledUserException(String message){
        super(message);
    }
}
