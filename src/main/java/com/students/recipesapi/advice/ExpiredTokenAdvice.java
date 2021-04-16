package com.students.recipesapi.advice;

import com.students.recipesapi.exception.ExpiredTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExpiredTokenAdvice {
    @ResponseBody
    @ExceptionHandler(ExpiredTokenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    String alreadyExistsHandler(ExpiredTokenException e) {
        return e.getMessage();
    }
}
