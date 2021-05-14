package com.students.recipesapi.model;

import com.students.recipesapi.entity.Product;
import com.students.recipesapi.entity.Recipe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.postgresql.util.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public RecipeModel(Recipe recipe) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.description = recipe.getDescription();
        this.ingredients = recipe.getIngredients()
                .stream()
                .map(Product::getId)
                .collect(Collectors.toList());
        this.quantities = new ArrayList<>(recipe.getQuantities());
        this.tags = new ArrayList<>(recipe.getTags());

        if (recipe.getImage() != null) {
            this.image = Base64.encodeBytes(recipe.getImage());
        } else {
            this.image = "";
        }
    }
}
