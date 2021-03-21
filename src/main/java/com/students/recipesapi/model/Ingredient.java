package com.students.recipesapi.model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany
    private Set<Recipe> recipe;

    @ManyToMany
    private Set<Product> product;
}
