package com.students.recipesapi.exception;

public class Base64DecodingException extends RuntimeException {
    public Base64DecodingException(String message) {
        super(message);
    }
}
