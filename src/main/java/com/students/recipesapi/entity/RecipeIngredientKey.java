package com.students.recipesapi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeIngredientKey implements Serializable {
    @Column(name = "recipe_id")
    Long recipeId;

    @Column(name = "product_id")
    Long productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeIngredientKey that = (RecipeIngredientKey) o;
        return Objects.equals(recipeId, that.recipeId) && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipeId, productId);
    }
}
