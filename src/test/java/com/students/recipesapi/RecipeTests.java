package com.students.recipesapi;

import com.students.recipesapi.entity.Product;
import com.students.recipesapi.entity.Recipe;
import com.students.recipesapi.entity.UserEntity;
import com.students.recipesapi.exception.InvalidInputException;
import com.students.recipesapi.exception.NotFoundException;
import com.students.recipesapi.model.ProductModel;
import com.students.recipesapi.model.RecipeModel;
import com.students.recipesapi.model.RegisterModel;
import com.students.recipesapi.service.ProductService;
import com.students.recipesapi.service.RecipeService;
import com.students.recipesapi.service.UserService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RecipeTests {
    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    RecipeService recipeService;

    private static final String exampleBase64Image = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAIAAAD8GO2jAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAADPSURBVEhL7Y1BEsUgCEN7/0v7GaE2YpDWqZs/fRsxCeQom/kKUqzgqOg8MrcU+w9cRhRK9qulGUFFpCvgifi6gC5ft7cyJkShaw10aTgvsCnABV4uENcFiGLvCdpj2kFdJyYFNgVEBaiTArXbMCEKoE4SaqfXhfUCxf4xUQb1MBEtI1EG9b8saHba8UIBRkeo67Z8wttLBTZVSIFNJ5OOxwXijTtUVFDXmGJSxRfY1DPREVN77hZQK9KRy55HqZteF+4WLLPlKPIVpGwuKOUHRXOc8NtT3yEAAAAASUVORK5CYII=";

    private final String registeredUsername = "recipeTests@gmail.com";
    private UserEntity registeredUser;
    private Product tomato;
    private Product carrot;

    private RecipeModel pizzaRecipeModel;
    private Recipe exampleExistingRecipe;

    @BeforeAll
    void setup() {
        registeredUser = userService.register(new RegisterModel(registeredUsername, "12345678"));

        tomato = productService.create(
                registeredUsername,
                new ProductModel(0L, "Pomidor", "12345678", "imageima")
        );

        carrot = productService.create(
                registeredUsername,
                new ProductModel(0L, "Marchewka", "87654321", "imageima")
        );

        pizzaRecipeModel = new RecipeModel(
                0L,
                "Pizza",
                "Opis",
                Arrays.asList(tomato.getId(), carrot.getId()),
                Arrays.asList("5", "2"),
                Arrays.asList("pyszne", "jedzenie"),
                exampleBase64Image
        );
    }

    @Test
    void findById_ExistingRecipeId_ReturnDesiredRecipe() {
        // Given
        Long recipeId = recipeService.create(registeredUsername, pizzaRecipeModel).getId();

        // When
        Recipe recipe = recipeService.findById(recipeId);

        // Then
        assertThat(recipe.getAuthor().getUsername()).isEqualTo(registeredUsername);
        assertThat(recipe.getTitle()).isEqualTo(pizzaRecipeModel.getTitle());
        assertThat(recipe.getDescription()).isEqualTo(pizzaRecipeModel.getDescription());
        assertThat(recipe.getTags()).containsExactlyInAnyOrderElementsOf(pizzaRecipeModel.getTags());
        assertThat(recipe.getIngredients().stream().map(key -> key.getProduct().getId())).containsExactlyInAnyOrderElementsOf(pizzaRecipeModel.getIngredients());
        assertThat(recipe.getImageBase64()).isEqualTo(pizzaRecipeModel.getImage());
    }

    @Test
    void findById_NonExistingRecipeId_ThrowNotFoundException() {
        assertThrows(
                NotFoundException.class,
                () -> recipeService.findById(Long.MAX_VALUE)
        );
    }

    @Test
    void create_NullRecipeModel_ThrowInvalidInputException() {
        // When
        RecipeModel recipeModel = null;

        // Then
        assertThrows(
                InvalidInputException.class,
                () -> recipeService.create(registeredUsername, recipeModel)
        );
    }

    @Test
    void create_NullTitle_ThrowInvalidInputException() {
        // When
        RecipeModel recipeModel = new RecipeModel(0L,
                null,
                "Description",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                exampleBase64Image);

        // Then
        assertThrows(
                InvalidInputException.class,
                () -> recipeService.create(registeredUsername, recipeModel)
        );
    }

    @Test
    void create_NullDescription_ThrowInvalidInputException() {
        // When
        RecipeModel recipeModel = new RecipeModel(0L,
                "Title",
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                exampleBase64Image);

        // Then
        assertThrows(
                InvalidInputException.class,
                () -> recipeService.create(registeredUsername, recipeModel)
        );
    }

    @Test
    void create_NullIngredients_ThrowInvalidInputException() {
        // When
        RecipeModel recipeModel = new RecipeModel(0L,
                "Title",
                "Description",
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                exampleBase64Image);

        // Then
        assertThrows(
                InvalidInputException.class,
                () -> recipeService.create(registeredUsername, recipeModel)
        );
    }

    @Test
    void create_NullQuantitites_ThrowInvalidInputException() {
        // When
        RecipeModel recipeModel = new RecipeModel(0L,
                "Title",
                "Description",
                Collections.emptyList(),
                null,
                Collections.emptyList(),
                exampleBase64Image);

        // Then
        assertThrows(
                InvalidInputException.class,
                () -> recipeService.create(registeredUsername, recipeModel)
        );
    }

    @Test
    void create_NullTags_ThrowInvalidInputException() {
        // When
        RecipeModel recipeModel = new RecipeModel(0L,
                "Title",
                "Description",
                Collections.emptyList(),
                Collections.emptyList(),
                null,
                exampleBase64Image);

        // Then
        assertThrows(
                InvalidInputException.class,
                () -> recipeService.create(registeredUsername, recipeModel)
        );
    }

    @Test
    void create_NullImage_ThrowInvalidInputException() {
        // When
        RecipeModel recipeModel = new RecipeModel(0L,
                "Title",
                "Description",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                null);

        // Then
        assertThrows(
                InvalidInputException.class,
                () -> recipeService.create(registeredUsername, recipeModel)
        );
    }

    @Test
    void create_DifferentNumberOfQuantitiesAndIngredients_ThrowInvalidInputException() {
        // When
        RecipeModel recipeModel = new RecipeModel(0L,
                "Title",
                "Description",
                Lists.list(tomato.getId(), carrot.getId()),
                Lists.list("55"),
                Collections.emptyList(),
                exampleBase64Image);

        // Then
        assertThrows(
                InvalidInputException.class,
                () -> recipeService.create(registeredUsername, recipeModel)
        );
    }

    @Test
    void create_NonexistentIngredient_ThrowInvalidInputException() {
        // When
        RecipeModel recipeModel = new RecipeModel(0L,
                "Title",
                "Description",
                Lists.list(tomato.getId(), Long.MAX_VALUE),
                Lists.list("55", "22"),
                Collections.emptyList(),
                exampleBase64Image);

        // Then
        assertThrows(
                InvalidInputException.class,
                () -> recipeService.create(registeredUsername, recipeModel)
        );
    }

    @Test
    void create_ValidRecipeModel_ReturnValidRecipe() {
        // Given
        RecipeModel recipeModel = new RecipeModel(0L,
                "Title",
                "Description",
                Lists.list(tomato.getId(), carrot.getId()),
                Lists.list("55", "22"),
                Lists.list("tag1111", "tagggg"),
                exampleBase64Image);

        // When
        Recipe recipe = recipeService.create(registeredUsername, recipeModel);

        // Then
        assertThat(recipe.getAuthor().getUsername()).isEqualTo(registeredUsername);
        assertThat(recipe.getTitle()).isEqualTo(recipeModel.getTitle());
        assertThat(recipe.getDescription()).isEqualTo(recipeModel.getDescription());
        assertThat(recipe.getTags()).containsExactlyInAnyOrderElementsOf(recipeModel.getTags());
        assertThat(recipe.getIngredients().stream().map(key -> key.getProduct().getId())).containsExactlyInAnyOrderElementsOf(recipeModel.getIngredients());
        assertThat(recipe.getImageBase64()).isEqualTo(recipeModel.getImage());
    }


/*
    @Test
    void findById_DeletedRecipe_ThrowNotFoundException() {
        // Given
        Recipe recipe = recipeService.create(registeredUsername, pizzaRecipeModel);
        recipeService.delete(registeredUsername, recipe.getId());

        // Then
        assertThrows(
                NotFoundException.class,
                () -> recipeService.findById(recipe.getId())
        );
    }
*/

    @Test
    void delete_NonexistentRecipe_ThrowNotFoundException() {
        assertThrows(
                NotFoundException.class,
                () -> recipeService.delete(registeredUsername, Long.MAX_VALUE)
        );
    }

    @Test
    void update_NonexistentRecipe_ThrowNotFoundException() {
        // Given
        RecipeModel recipeModel = new RecipeModel(
                Long.MAX_VALUE,
                "Title",
                "Description",
                Lists.list(tomato.getId()),
                Lists.list("5"),
                Lists.list("newtag"),
                exampleBase64Image);

        // Then
        assertThrows(
                NotFoundException.class,
                () -> recipeService.update(registeredUsername, recipeModel)
        );
    }

    @Test
    void update_ExistentRecipe_ReturnUpdatedRecipe() {
        // Given
        Recipe recipe = recipeService.create(registeredUsername, pizzaRecipeModel);
        RecipeModel recipeModel = new RecipeModel(
                recipe.getId(),
                "Title",
                "Description",
                Lists.list(tomato.getId()),
                Lists.list("5"),
                Lists.list("newtag"),
                exampleBase64Image);

        // When
        Recipe returnedRecipe = recipeService.update(registeredUsername, recipeModel);

        // Then
        assertThat(returnedRecipe.getAuthor().getUsername()).isEqualTo(registeredUsername);
        assertThat(returnedRecipe.getTitle()).isEqualTo(recipeModel.getTitle());
        assertThat(returnedRecipe.getDescription()).isEqualTo(recipeModel.getDescription());
        assertThat(returnedRecipe.getTags()).containsExactlyInAnyOrderElementsOf(recipeModel.getTags());
        assertThat(returnedRecipe.getIngredients().stream().map(key -> key.getProduct().getId())).containsExactlyInAnyOrderElementsOf(recipeModel.getIngredients());
        assertThat(returnedRecipe.getImageBase64()).isEqualTo(recipeModel.getImage());
    }
}
