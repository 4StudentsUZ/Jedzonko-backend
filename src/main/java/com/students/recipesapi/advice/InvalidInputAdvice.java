package com.students.recipesapi.advice;

import com.students.recipesapi.exception.AlreadyExistsException;
import com.students.recipesapi.exception.InvalidInputException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class InvalidInputAdvice {
    @ResponseBody
    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String invalidInputHandler(InvalidInputException e) {
        return e.getMessage();
    }
}
