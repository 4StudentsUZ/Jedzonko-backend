package com.students.recipesapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeIngredient {
    @EmbeddedId
    @JsonIgnore
    RecipeIngredientKey id;

    @ManyToOne
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id")
    @JsonIgnore
    Recipe recipe;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    Product product;

    String quantity;
}
