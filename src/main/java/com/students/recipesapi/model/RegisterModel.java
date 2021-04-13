package com.students.recipesapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class RegisterModel implements Serializable {
    private String username;
    private String password;
}
