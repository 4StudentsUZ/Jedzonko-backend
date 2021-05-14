package com.students.recipesapi.service;

import com.students.recipesapi.entity.Recipe;
import com.students.recipesapi.entity.UserEntity;
import com.students.recipesapi.exception.InvalidInputException;
import com.students.recipesapi.exception.NotFoundException;
import com.students.recipesapi.model.RecipeModel;
import com.students.recipesapi.repository.RecipeRepository;
import org.postgresql.util.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserService userService;
    private final ProductService productService;

    public RecipeService(RecipeRepository recipeRepository, UserService userService, ProductService productService) {
        this.recipeRepository = recipeRepository;
        this.userService = userService;
        this.productService = productService;
    }

    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    public Recipe findById(Long id) {
        return recipeRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Recipe with id %d not found.", id)));
    }

    @Transactional
    public Recipe create(String username, RecipeModel recipeModel) {
        validateRecipeModelForCreate(recipeModel);
        UserEntity author = requireUser(username);

        Recipe recipe = new Recipe();
        recipe.setTitle(recipeModel.getTitle());
        recipe.setDescription(recipeModel.getDescription());
        recipe.setIngredients(productService.findAllById(recipeModel.getIngredients()));
        recipe.setQuantities(recipeModel.getQuantities());
        recipe.setTags(recipeModel.getTags());
        recipe.setAuthor(author);
        recipe.setImage(Base64.decode(recipeModel.getImage()));

        return recipeRepository.save(recipe);
    }

    public void delete(String username, Long recipeId) {
        Recipe recipe = findById(recipeId);
        UserEntity author = userService.findByUsername(username);
        validateAuthorMatch(author, recipe);
        recipeRepository.deleteById(recipeId);
    }

    public void update(String username, RecipeModel recipeModel) {
        validateRecipeModelForUpdate(recipeModel);
        UserEntity author = requireUser(username);
        Recipe originalRecipe = findById(recipeModel.getId());
        validateAuthorMatch(author, originalRecipe);

        if (recipeModel.getTitle() != null) originalRecipe.setTitle(recipeModel.getTitle());
        if (recipeModel.getDescription() != null) originalRecipe.setDescription(recipeModel.getDescription());
        if (recipeModel.getIngredients() != null) {
            originalRecipe.setIngredients(productService.findAllById(recipeModel.getIngredients()));
            originalRecipe.setQuantities(recipeModel.getQuantities());
        }
        if (recipeModel.getTags() != null)
            originalRecipe.setTags(recipeModel.getTags());
        if (recipeModel.getImage() != null) originalRecipe.setImage(recipeModel.getImage());

        recipeRepository.save(originalRecipe);
    }

    private void validateRecipeModelForCreate(RecipeModel recipeModel) {
        if (recipeModel == null)
            throw new InvalidInputException("No recipe model has been provided.");
        if (recipeModel.getTitle() == null)
            throw new InvalidInputException("Recipe title has not been provided.");
        if (recipeModel.getDescription() == null)
            throw new InvalidInputException("Recipe description has not been provided.");
        if (recipeModel.getIngredients() == null)
            throw new InvalidInputException("Recipe ingredients have not been provided.");
        if (recipeModel.getQuantities() == null)
            throw new InvalidInputException("Recipe quantities have not been provided.");
        if (recipeModel.getIngredients().size() > 50)
            throw new InvalidInputException("Tried to add a recipe with more than 50 products.");
        if (recipeModel.getTags() == null)
            throw new InvalidInputException("Tried to add a recipe without tags.");
        if (recipeModel.getImage() == null)
            throw new InvalidInputException("Tried to add a recipe without an image.");
        if (recipeModel.getQuantities().size() != recipeModel.getIngredients().size())
            throw new InvalidInputException("Quantity list doesnt match the size of ingredient list.");
    }

    private void validateRecipeModelForUpdate(RecipeModel recipeModel) {
        if (recipeModel.getIngredients() != null && recipeModel.getIngredients().size() > 50) {
            throw new InvalidInputException("Tried to add a recipe with more than 50 products.");
        }
        if (recipeModel.getIngredients() != null) {
            if (recipeModel.getQuantities() == null)
                throw new InvalidInputException("Recipe quantities have not been provided.");
            if (recipeModel.getQuantities().size() != recipeModel.getIngredients().size())
                throw new InvalidInputException("Quantity list doesnt match the size of ingredient list.");
        }
    }

    private void validateAuthorMatch(UserEntity author, Recipe recipe) {
        if (!author.getUsername().equals(recipe.getAuthor().getUsername())) {
            throw new InvalidInputException("Tried to act on a recipe the user has was not authorized to.");
        }
    }

    private UserEntity requireUser(String username) {
        return userService.findByUsername(username);
    }
}
