package com.students.recipesapi.exception;

public class InvalidJwtAuthenticationException extends Throwable {
    public InvalidJwtAuthenticationException(String msg) {
        super(msg);
    }
}
