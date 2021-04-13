package com.students.recipesapi.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LoginModel implements Serializable {
    private String username;
    private String password;
}
