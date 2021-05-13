package com.students.recipesapi.advice;

import com.students.recipesapi.exception.Base64DecodingException;
import com.students.recipesapi.exception.InvalidInputException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class Base64DecodingAdvice {
    @ResponseBody
    @ExceptionHandler(Base64DecodingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String invalidInputHandler(Base64DecodingException e) {
        return e.getMessage();
    }
}
