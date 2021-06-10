package com.students.recipesapi.service;

import com.students.recipesapi.entity.*;
import com.students.recipesapi.exception.InvalidInputException;
import com.students.recipesapi.exception.NotFoundException;
import com.students.recipesapi.model.RecipeModel;
import com.students.recipesapi.repository.CommentRepository;
import com.students.recipesapi.repository.RatingRepository;
import com.students.recipesapi.repository.RecipeIngredientRepository;
import com.students.recipesapi.repository.RecipeRepository;
import org.postgresql.util.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final RecipeIngredientRepository ingredientRepository;
    private final UserService userService;
    private final ProductService productService;
    private final RatingRepository ratingRepository;

    public RecipeService(RecipeRepository recipeRepository, CommentRepository commentRepository, RecipeIngredientRepository ingredientRepository, UserService userService, ProductService productService, RatingRepository ratingRepository) {
        this.recipeRepository = recipeRepository;
        this.commentRepository = commentRepository;
        this.ingredientRepository = ingredientRepository;
        this.userService = userService;
        this.productService = productService;
        this.ratingRepository = ratingRepository;
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
    public List<Recipe> findByQuerySorted(String query, String sort, String direction) {
        query = query.toLowerCase(Locale.ROOT);
        sort = sort.toLowerCase(Locale.ROOT);
        direction = direction.toLowerCase(Locale.ROOT);

        List<Recipe> recipes = recipeRepository.findAllByQuery(query);

        if (!sort.isEmpty()) {
            Comparator<Recipe> comparator = null;

            switch (sort) {
                case "title":
                    comparator = Comparator.comparing(Recipe::getTitle);
                    break;
                case "creationdate":
                    comparator = (r1, r2) -> {
                        LocalDateTime date1 = LocalDateTime.parse(r1.getCreationDate());
                        LocalDateTime date2 = LocalDateTime.parse(r2.getCreationDate());
                        return date1.compareTo(date2);
                    };
                    break;
                case "rating":
                    for (Recipe r : recipes) {
                        r.setRating(ratingRepository.getAvgForRecipe(r));
                    }

                    comparator = Comparator.comparing(Recipe::getRating);
                    break;
            }

            if (direction.equals("desc") && comparator != null) {
                comparator = comparator.reversed();
            }

            recipes.sort(comparator);
        }

        return recipes;
    }

    public Recipe create(String username, RecipeModel recipeModel) {
        validateRecipeModelForCreate(recipeModel);
        UserEntity author = requireUser(username);

        Recipe recipe = new Recipe();
        recipe.setTitle(recipeModel.getTitle());
        recipe.setDescription(recipeModel.getDescription());
        recipe.setTags(new HashSet<>(recipeModel.getTags()));
        recipe.setAuthor(author);
        recipe.setImage(Base64.decode(recipeModel.getImage()));
        recipe.setCreationDate(LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString());
        recipe.setModificationDate(LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString());

        recipe = recipeRepository.save(recipe);

        try {
            Set<RecipeIngredient> ingredients = new HashSet<>();
            for (int ingredientIndex = 0; ingredientIndex < recipeModel.getIngredients().size(); ingredientIndex++) {
                RecipeIngredient ingredient = new RecipeIngredient();
                Product product = productService.findById(recipeModel.getIngredients().get(ingredientIndex));
                ingredient.setId(new RecipeIngredientKey(recipe.getId(), product.getId()));
                ingredient.setRecipe(recipe);
                ingredient.setProduct(product);
                ingredient.setQuantity(recipeModel.getQuantities().get(ingredientIndex));
                ingredient = ingredientRepository.save(ingredient);
                ingredients.add(ingredient);
            }
            recipe.setIngredients(ingredients);
        } catch (Exception e) {
            recipeRepository.delete(recipe);
            throw new InvalidInputException("Couldn't find the necessary ingredients.");
        }

        return recipe;
    }

    public void delete(String username, Long recipeId) {
        Recipe recipe = findById(recipeId);
        UserEntity author = userService.findByUsername(username);
        validateAuthorMatch(author, recipe);
        deleteIngredientsForRecipe(recipe);
        deleteCommentsForRecipe(recipe);
        deleteRatingsForRecipe(recipe);
        recipeRepository.deleteById(recipeId);
    }

    public Recipe update(String username, RecipeModel recipeModel) {
        validateRecipeModelForUpdate(recipeModel);
        UserEntity author = requireUser(username);
        Recipe originalRecipe = findById(recipeModel.getId());
        validateAuthorMatch(author, originalRecipe);

        if (recipeModel.getTitle() != null) originalRecipe.setTitle(recipeModel.getTitle());
        if (recipeModel.getDescription() != null) originalRecipe.setDescription(recipeModel.getDescription());
        if (recipeModel.getIngredients() != null) {
            deleteIngredientsForRecipe(originalRecipe);
            Set<RecipeIngredient> ingredients = new HashSet<>();
            for (int ingredientIndex = 0; ingredientIndex < recipeModel.getIngredients().size(); ingredientIndex++) {
                RecipeIngredient ingredient = new RecipeIngredient();
                Product product = productService.findById(recipeModel.getIngredients().get(ingredientIndex));
                ingredient.setId(new RecipeIngredientKey(originalRecipe.getId(), product.getId()));
                ingredient.setRecipe(originalRecipe);
                ingredient.setProduct(product);
                ingredient.setQuantity(recipeModel.getQuantities().get(ingredientIndex));
                ingredient = ingredientRepository.save(ingredient);
                ingredients.add(ingredient);
            }
            originalRecipe.setIngredients(ingredients);
        }
        if (recipeModel.getTags() != null)
            originalRecipe.setTags(new LinkedHashSet<>(recipeModel.getTags()));
        if (recipeModel.getImage() != null) originalRecipe.setImage(recipeModel.getImage());
        originalRecipe.setModificationDate(LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString());

        return recipeRepository.save(originalRecipe);
    }

    @Transactional
    public void deleteIngredientsForRecipe(Recipe recipe) {
        List<RecipeIngredient> ingredients = ingredientRepository.findByRecipeId(recipe.getId());
        ingredientRepository.deleteAll(ingredients);
    }

    @Transactional
    public void deleteCommentsForRecipe(Recipe recipe) {
        List<Comment> comments = commentRepository.findForRecipe(recipe.getId());
        commentRepository.deleteAll(comments);
    }

    @Transactional
    public void deleteRatingsForRecipe(Recipe recipe) {
        List<Rating> ratings = ratingRepository.findForRecipe(recipe.getId());
        ratingRepository.deleteAll(ratings);
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
