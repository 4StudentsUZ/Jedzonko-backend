package com.students.recipesapi.model;

import com.students.recipesapi.entity.Product;
import com.students.recipesapi.entity.Recipe;
import com.students.recipesapi.entity.RecipeIngredient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResponse {
    private Long id;
    private String title;
    private String description;
    private List<Product> ingredients;
    private List<String> quantities;
    private Set<String> tags;
    private String image;

    public RecipeResponse(Recipe recipe) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.description = recipe.getDescription();
        this.ingredients = recipe.getIngredients().stream().map(RecipeIngredient::getProduct).collect(Collectors.toList());
        this.quantities = recipe.getIngredients().stream().map(RecipeIngredient::getQuantity).collect(Collectors.toList());
        this.tags = recipe.getTags();
        this.image = recipe.getImageBase64();
    }
}
