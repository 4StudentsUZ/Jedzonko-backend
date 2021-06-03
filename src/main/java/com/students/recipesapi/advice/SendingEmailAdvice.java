package com.students.recipesapi.advice;

import com.students.recipesapi.exception.SendingEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class SendingEmailAdvice {
    @ResponseBody
    @ExceptionHandler(SendingEmailException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String invalidInputHandler(SendingEmailException e) {
        return e.getMessage();
    }
}
