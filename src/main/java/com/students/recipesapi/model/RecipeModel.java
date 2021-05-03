package com.students.recipesapi.model;

import com.students.recipesapi.entity.Recipe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.postgresql.util.Base64;

import java.util.ArrayList;
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
    private String image;

    public RecipeModel(Recipe recipe) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.description = recipe.getDescription();
        this.ingredients = new ArrayList<>(recipe.getIngredients());
        if (recipe.getImage() != null) {
            this.image = Base64.encodeBytes(recipe.getImage());
        } else {
            this.image = "";
        }
    }
}
