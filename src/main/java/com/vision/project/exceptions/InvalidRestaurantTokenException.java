package com.vision.project.exceptions;

public class InvalidRestaurantTokenException extends RuntimeException{
    public InvalidRestaurantTokenException(String message){
        super(message);
    }
}
