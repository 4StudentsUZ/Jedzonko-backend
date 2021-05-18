package com.students.recipesapi;

import com.students.recipesapi.entity.Product;
import com.students.recipesapi.entity.Recipe;
import com.students.recipesapi.entity.UserEntity;
import com.students.recipesapi.exception.InvalidInputException;
import com.students.recipesapi.exception.NotFoundException;
import com.students.recipesapi.model.ProductModel;
import com.students.recipesapi.model.RatingModel;
import com.students.recipesapi.model.RecipeModel;
import com.students.recipesapi.model.RegisterModel;
import com.students.recipesapi.service.ProductService;
import com.students.recipesapi.service.RatingService;
import com.students.recipesapi.service.RecipeService;
import com.students.recipesapi.service.UserService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RatingTests {
    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    RecipeService recipeService;

    @Autowired
    RatingService ratingService;

    private static final String exampleBase64Image = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAIAAAD8GO2jAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAADPSURBVEhL7Y1BEsUgCEN7/0v7GaE2YpDWqZs/fRsxCeQom/kKUqzgqOg8MrcU+w9cRhRK9qulGUFFpCvgifi6gC5ft7cyJkShaw10aTgvsCnABV4uENcFiGLvCdpj2kFdJyYFNgVEBaiTArXbMCEKoE4SaqfXhfUCxf4xUQb1MBEtI1EG9b8saHba8UIBRkeo67Z8wttLBTZVSIFNJ5OOxwXijTtUVFDXmGJSxRfY1DPREVN77hZQK9KRy55HqZteF+4WLLPlKPIVpGwuKOUHRXOc8NtT3yEAAAAASUVORK5CYII=";

    private final String registeredUsername = "ratingTests@gmail.com";

    private RecipeModel pizzaRecipeModel;
    private Recipe pizzaRecipe;

    @BeforeAll
    void setup() {
        userService.register(new RegisterModel(registeredUsername, "12345678"));

        Product tomato = productService.create(
                registeredUsername,
                new ProductModel(0L, "Pomidor", "12345678", "imageima")
        );

        Product carrot = productService.create(
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

        pizzaRecipe = recipeService.create(registeredUsername, pizzaRecipeModel);
    }

    @Test
    void rate_NullRecipeId_ThrowInvalidInputException() {
        // Given
        RatingModel ratingModel = new RatingModel(null, 4.0);

        // Then
        assertThrows(
                InvalidInputException.class,
                () -> ratingService.rate(registeredUsername, ratingModel)
        );
    }

    @Test
    void rate_NullRating_ThrowInvalidInputException() {
        // When
        RatingModel ratingModel = new RatingModel(pizzaRecipe.getId(), null);

        // Then
        assertThrows(
                InvalidInputException.class,
                () -> ratingService.rate(registeredUsername, ratingModel)
        );
    }

    @Test
    void getAvgForRecipe_NonexistentRecipe_ThrowNotFoundException() {
        // When
        long recipeId = Long.MAX_VALUE;

        // Then
        assertThrows(
                NotFoundException.class,
                () -> ratingService.getAvgForRecipe(recipeId)
        );
    }

    @Test
    void getAvgForRecipe_NotRatedRecipe_ReturnZero() {
        // Given
        Recipe recipe = recipeService.create(registeredUsername, pizzaRecipeModel);

        // When
        double averageRating = ratingService.getAvgForRecipe(recipe.getId());

        // Then
        assertThat(averageRating).isEqualTo(0.0);
    }

    @SuppressWarnings("unused")
    private Stream<Arguments> provideRatingsAndTheirAverage() {
        return Stream.of(
                Arguments.of(0, Lists.list(2.0), 2),
                Arguments.of(1, Lists.list(2.0, 3.0, 4.0), 3),
                Arguments.of(2, Lists.list(2.0, 5.0), 3.5),
                Arguments.of(3, Lists.list(1.0, 8.0, 7.0, 5.0, 1.0), (1 + 8 + 7 + 5 + 1) / 5.0),
                Arguments.of(4, Lists.list(1.0), 1),
                Arguments.of(5, Lists.list(3.0, 3.0, 3.0, 3.0, 3.0, 3.0), 3),
                Arguments.of(6, Lists.list(1.0, 2.0, 4.0), (1 + 2 + 4) / 3.0),
                Arguments.of(7, Lists.list(1.0, 8.0), 4.5)
        );
    }

    @ParameterizedTest
    @MethodSource("provideRatingsAndTheirAverage")
    void rate_RecipeRatedANumberOfTimes_ReturnValidRating(int testId, List<Double> ratings, double targetAverage) {
        // Given
        Recipe recipe = recipeService.create(registeredUsername, pizzaRecipeModel);

        // When
        for (int i = 0; i < ratings.size(); i++) {
            Double rating = ratings.get(i);
            UserEntity user = userService.register(new RegisterModel("getAvgForRecipe_" + testId + "RecipeRatedANumberOfTimes" + i + "@email.com", "12345678"));
            ratingService.rate(user.getUsername(), new RatingModel(recipe.getId(), rating));
        }
        double averageRating = ratingService.getAvgForRecipe(recipe.getId());

        // Then
        assertThat(averageRating).isEqualTo(targetAverage);
    }
}
