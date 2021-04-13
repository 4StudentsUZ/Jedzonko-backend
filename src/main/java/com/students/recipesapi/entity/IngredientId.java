package com.students.recipesapi.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class IngredientId implements Serializable {
    private int recipe;
    private int product;
}
