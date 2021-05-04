package com.students.recipesapi.service;

import com.students.recipesapi.entity.Comment;
import com.students.recipesapi.entity.Rating;
import com.students.recipesapi.entity.Recipe;
import com.students.recipesapi.entity.UserEntity;
import com.students.recipesapi.exception.InvalidInputException;
import com.students.recipesapi.exception.NotFoundException;
import com.students.recipesapi.model.CommentModel;
import com.students.recipesapi.model.RatingModel;
import com.students.recipesapi.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final RecipeService recipeService;
    private final UserService userService;

    public RatingService(RatingRepository ratingRepository, RecipeService recipeService, UserService userService) {
        this.ratingRepository = ratingRepository;
        this.recipeService = recipeService;
        this.userService = userService;
    }

    public double getAvgForRecipe(Long recipeId) {
        Recipe recipe = recipeService.findById(recipeId);
        return ratingRepository.getAvgForRecipe(recipe);
    }

    public Rating rate(String username, RatingModel ratingModel) {
        validateRating(ratingModel);
        UserEntity userEntity = userService.findByUsername(username);
        Recipe recipe = recipeService.findById(ratingModel.recipeId);
        Optional<Rating> optionalRating = ratingRepository.findByUserAndRecipe(userEntity, recipe);
        Rating rating;
        if (!optionalRating.isPresent()) {
            rating = new Rating();
            rating.setRecipe(recipe);
            rating.setUser(userEntity);
            rating.setRating(ratingModel.rating);
        }
        else {
            rating = optionalRating.get();
            rating.setRating(ratingModel.rating);
        }
        validateAuthority(username, rating);

        return ratingRepository.save(rating);
    }

    private void validateRating(RatingModel ratingModel) {
        if (ratingModel.getRecipeId() == null) {
            throw new InvalidInputException("Recipe id was not provided");
        }
    }

    private void validateAuthority(String authorUsername, Rating rating) {
        if (!authorUsername.equals(rating.getUser().getUsername())) {
            throw new InvalidInputException("Tried to act on a recipe the user has was not authorized to.");
        }
    }
}
