package com.students.recipesapi.controller;

import com.students.recipesapi.entity.Recipe;
import com.students.recipesapi.entity.UserEntity;
import com.students.recipesapi.model.*;
import com.students.recipesapi.service.RecipeService;
import com.students.recipesapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/get/all")
    @ResponseBody
    ResponseEntity<List<RecipeModel>> all() {
        List<Recipe> recipes = recipeService.findAll();
        List<RecipeModel> recipeModels = recipes
                .stream()
                .map(RecipeModel::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(recipeModels);
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    ResponseEntity<RecipeModel> one(@PathVariable Long id) {
        Recipe recipe = recipeService.findById(id);
        RecipeModel recipeModel = new RecipeModel(recipe);
        return ResponseEntity.ok(recipeModel);
    }

    @PostMapping(value = "/create/", consumes = "application/json", produces = "application/json")
    ResponseEntity<RecipeModel> create(@RequestBody RecipeModel recipe, Principal principal) {
        Recipe returnedRecipe = recipeService.create(principal.getName(), recipe);
        RecipeModel returnedRecipeModel = new RecipeModel(returnedRecipe);
        return ResponseEntity.ok(returnedRecipeModel);
    }

    @PutMapping(value = "/update/{recipeId}", consumes = "application/json", produces = "application/json")
    ResponseEntity<String> update(@PathVariable Long recipeId, @RequestBody RecipeModel recipeModel, Principal principal) {
        recipeModel.setId(recipeId);
        recipeService.update(principal.getName(), recipeModel);
        return ResponseEntity.ok("Successfully updated the recipe.");
    }

    @DeleteMapping(value = "/delete/{recipeId}", consumes = "application/json", produces = "application/json")
    ResponseEntity<String> delete(@PathVariable Long recipeId, Principal principal) {
        recipeService.delete(principal.getName(), recipeId);
        return ResponseEntity.ok("Successfully deleted the recipe.");
    }
}
