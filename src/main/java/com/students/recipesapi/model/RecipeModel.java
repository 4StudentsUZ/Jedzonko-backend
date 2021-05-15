package com.students.recipesapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeModel {
    private Long id;
    private String title;
    private String description;
    private List<Long> ingredients;
    private List<String> quantities;
    private List<String> tags;
    private String image;
}
