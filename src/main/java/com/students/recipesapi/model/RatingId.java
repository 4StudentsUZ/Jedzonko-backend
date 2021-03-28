package com.students.recipesapi.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class RatingId implements Serializable {
    private int user;
    private int recipe;
}

