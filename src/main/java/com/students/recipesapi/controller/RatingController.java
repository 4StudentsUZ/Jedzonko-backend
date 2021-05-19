package com.students.recipesapi.controller;

import com.students.recipesapi.entity.Rating;
import com.students.recipesapi.model.RatingModel;
import com.students.recipesapi.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;

@RestController
@RequestMapping("/ratings")
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping(value = "/get/forRecipe/{recipeId}", produces = "application/json")
    ResponseEntity<HashMap<String, Object>> getAvgForRecipe(@PathVariable Long recipeId) {
        double avg = ratingService.getAvgForRecipe(recipeId);
        HashMap<String, Object> result = new HashMap<>();
        result.put("average", avg);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/rate", consumes = "application/json", produces = "application/json")
    ResponseEntity<HashMap<String, Object>> create(@RequestBody RatingModel ratingModel, Principal principal) {
        ratingService.rate(principal.getName(), ratingModel);
        double avg = ratingService.getAvgForRecipe(ratingModel.getRecipeId());
        HashMap<String, Object> result = new HashMap<>();
        result.put("average", avg);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/get/myRatingForRecipe/{recipeId}", produces = "application/json")
    ResponseEntity<HashMap<String, Object>> getMyRatingForRecipe(@PathVariable Long recipeId, Principal principal) {
        Rating rating = ratingService.getUserRatingForRecipe(principal.getName(), recipeId);
        HashMap<String, Object> result = new HashMap<>();
        result.put("rating", rating.getRating());
        return ResponseEntity.ok(result);
    }
}
