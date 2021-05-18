package com.students.recipesapi;

import com.students.recipesapi.entity.Comment;
import com.students.recipesapi.entity.Product;
import com.students.recipesapi.entity.Recipe;
import com.students.recipesapi.exception.InvalidInputException;
import com.students.recipesapi.exception.NotFoundException;
import com.students.recipesapi.model.CommentModel;
import com.students.recipesapi.model.ProductModel;
import com.students.recipesapi.model.RecipeModel;
import com.students.recipesapi.model.RegisterModel;
import com.students.recipesapi.service.CommentService;
import com.students.recipesapi.service.ProductService;
import com.students.recipesapi.service.RecipeService;
import com.students.recipesapi.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommentTests {
    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    RecipeService recipeService;

    @Autowired
    CommentService commentService;

    private static final String exampleBase64Image = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAIAAAD8GO2jAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAADPSURBVEhL7Y1BEsUgCEN7/0v7GaE2YpDWqZs/fRsxCeQom/kKUqzgqOg8MrcU+w9cRhRK9qulGUFFpCvgifi6gC5ft7cyJkShaw10aTgvsCnABV4uENcFiGLvCdpj2kFdJyYFNgVEBaiTArXbMCEKoE4SaqfXhfUCxf4xUQb1MBEtI1EG9b8saHba8UIBRkeo67Z8wttLBTZVSIFNJ5OOxwXijTtUVFDXmGJSxRfY1DPREVN77hZQK9KRy55HqZteF+4WLLPlKPIVpGwuKOUHRXOc8NtT3yEAAAAASUVORK5CYII=";

    private final String registeredUsername = "commentTests@gmail.com";

    private RecipeModel pizzaRecipeModel;

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
    }

    @Test
    void findForRecipe_NonexistentRecipe_ReturnEmptyList() {
        // Given
        long recipeId = Long.MAX_VALUE;

        // When
        List<Comment> returnedComments = commentService.findForRecipe(recipeId);

        // Then
        assertThat(returnedComments).isEmpty();
    }

    @Test
    void findForRecipe_RecipeWithOneComment_ReturnAListWithThatComment() {
        // Given
        Recipe recipe = recipeService.create(registeredUsername, pizzaRecipeModel);
        CommentModel commentModel = new CommentModel(0L, "Test", recipe.getId());
        Comment comment = commentService.create(registeredUsername, commentModel);

        // When
        List<Comment> returnedComments = commentService.findForRecipe(recipe.getId());

        // Then
        assertThat(returnedComments).hasSize(1);
        Comment returnedComment = returnedComments.get(0);
        assertThat(returnedComment.getId()).isEqualTo(comment.getId());
        assertThat(returnedComment.getAuthor().getUsername()).isEqualTo(comment.getAuthor().getUsername());
        assertThat(returnedComment.getCreationDate()).isEqualTo(comment.getCreationDate());
        assertThat(returnedComment.getContent()).isEqualTo(comment.getContent());
        assertThat(returnedComment.getRecipe().getId()).isEqualTo(comment.getRecipe().getId());
    }

    @Test
    void findForRecipe_RecipeWithMultipleComments_ReturnAListWithTheseComments() {
        // Given
        Recipe recipe = recipeService.create(registeredUsername, pizzaRecipeModel);
        Comment[] comments = new Comment[15];
        for (int i = 0; i < 15; i++) {
            CommentModel commentModel = new CommentModel(0L, "Test" + i, recipe.getId());
            comments[i] = commentService.create(registeredUsername, commentModel);
        }

        // When
        List<Comment> returnedComments = commentService.findForRecipe(recipe.getId());

        // Then
        assertThat(returnedComments).hasSize(15);
        for (int i = 0; i < 15; i++) {
            Comment returnedComment = returnedComments.get(i);
            assertThat(returnedComment.getId()).isEqualTo(comments[i].getId());
            assertThat(returnedComment.getAuthor().getUsername()).isEqualTo(comments[i].getAuthor().getUsername());
            assertThat(returnedComment.getCreationDate()).isEqualTo(comments[i].getCreationDate());
            assertThat(returnedComment.getContent()).isEqualTo(comments[i].getContent());
            assertThat(returnedComment.getRecipe().getId()).isEqualTo(comments[i].getRecipe().getId());
        }
    }

    @Test
    void findById_NonexistentComment_ThrowNotFoundException() {
        // When
        long commentId = Long.MAX_VALUE;

        // Then
        assertThrows(
                NotFoundException.class,
                () -> commentService.findById(commentId)
        );
    }

    @Test
    void create_ContentIsNull_ThrowInvalidInputException() {
        // When
        Recipe recipe = recipeService.create(registeredUsername, pizzaRecipeModel);
        CommentModel commentModel = new CommentModel(0L, null, recipe.getId());

        // Then
        assertThrows(
                InvalidInputException.class,
                () -> commentService.create(registeredUsername, commentModel)
        );
    }

    @Test
    void create_RecipeIdIsNull_ThrowInvalidInputException() {
        // When
        CommentModel commentModel = new CommentModel(0L, "Test", null);

        // Then
        assertThrows(
                InvalidInputException.class,
                () -> commentService.create(registeredUsername, commentModel)
        );
    }

    @Test
    void create_CommentModelIsValid_ReturnMatchingComment() {
        // Given
        Recipe recipe = recipeService.create(registeredUsername, pizzaRecipeModel);
        CommentModel commentModel = new CommentModel(0L, "Test", recipe.getId());

        // When
        Comment returnedComment = commentService.create(registeredUsername, commentModel);

        // Then
        assertThat(returnedComment.getAuthor().getUsername()).isEqualTo(registeredUsername);
        assertThat(returnedComment.getContent()).isEqualTo(commentModel.getContent());
        assertThat(returnedComment.getRecipe().getId()).isEqualTo(commentModel.getRecipeId());
    }

    @Test
    void create_CommentModelIsValid_CanFindTheCommentById() {
        // Given
        Recipe recipe = recipeService.create(registeredUsername, pizzaRecipeModel);
        CommentModel commentModel = new CommentModel(0L, "Test", recipe.getId());

        // When
        Comment returnedComment = commentService.create(registeredUsername, commentModel);

        // Then
        assertThat(commentService.findById(returnedComment.getId())).isNotNull();
    }

    @Test
    void update_ValidCommentModel_ReturnMatchingComment() {
        // Given
        Recipe recipe = recipeService.create(registeredUsername, pizzaRecipeModel);
        CommentModel commentModel = new CommentModel(0L, "Test", recipe.getId());
        Comment comment = commentService.create(registeredUsername, commentModel);
        CommentModel updateModel = new CommentModel(comment.getId(), "UpdatedContent", recipe.getId());

        // When
        Comment updatedComment = commentService.update(registeredUsername, updateModel);

        // Then
        assertThat(updatedComment.getAuthor().getUsername()).isEqualTo(registeredUsername);
        assertThat(updatedComment.getContent()).isEqualTo(updateModel.getContent());
        assertThat(updatedComment.getRecipe().getId()).isEqualTo(updateModel.getRecipeId());
    }

    @Test
    void update_ValidCommentModel_CanGetTheUpdatedCommentAndItMatches() {
        // Given
        Recipe recipe = recipeService.create(registeredUsername, pizzaRecipeModel);
        CommentModel commentModel = new CommentModel(0L, "Test", recipe.getId());
        Comment comment = commentService.create(registeredUsername, commentModel);
        CommentModel updateModel = new CommentModel(comment.getId(), "UpdatedContent", recipe.getId());

        // When
        commentService.update(registeredUsername, updateModel);

        // Then
        Comment foundComment = commentService.findById(comment.getId());
        assertThat(foundComment.getAuthor().getUsername()).isEqualTo(registeredUsername);
        assertThat(foundComment.getContent()).isEqualTo(updateModel.getContent());
        assertThat(foundComment.getRecipe().getId()).isEqualTo(updateModel.getRecipeId());
    }

    @Test
    void update_ContentIsNull_ThrowInvalidInputException() {
        // Given
        Recipe recipe = recipeService.create(registeredUsername, pizzaRecipeModel);
        CommentModel commentModel = new CommentModel(0L, "Test", recipe.getId());
        Comment comment = commentService.create(registeredUsername, commentModel);
        CommentModel updateModel = new CommentModel(comment.getId(), null, recipe.getId());

        // Then
        assertThrows(
                InvalidInputException.class,
                () -> commentService.update(registeredUsername, updateModel)
        );
    }

    @Test
    void update_CommentIdIsNull_ThrowInvalidInputException() {
        // Given
        Recipe recipe = recipeService.create(registeredUsername, pizzaRecipeModel);
        CommentModel commentModel = new CommentModel(0L, "Test", recipe.getId());
        Comment comment = commentService.create(registeredUsername, commentModel);
        CommentModel updateModel = new CommentModel(null, "NewContent", recipe.getId());

        // Then
        assertThrows(
                InvalidInputException.class,
                () -> commentService.update(registeredUsername, updateModel)
        );
    }

    @Test
    void delete_NonexistentComment_ThrowNotFoundException() {
        // When
        long commentId = Long.MAX_VALUE;

        // Then
        assertThrows(
                NotFoundException.class,
                () -> commentService.delete(registeredUsername, commentId)
        );
    }

    @Test
    void delete_ExistentComment_ThrowNotFoundWhenTryingToGetItById() {
        // Given
        Recipe recipe = recipeService.create(registeredUsername, pizzaRecipeModel);
        CommentModel commentModel = new CommentModel(0L, "Test", recipe.getId());
        Comment comment = commentService.create(registeredUsername, commentModel);

        // When
        commentService.delete(registeredUsername, comment.getId());

        // Then
        assertThrows(
                NotFoundException.class,
                () -> commentService.findById(comment.getId())
        );
    }
}
