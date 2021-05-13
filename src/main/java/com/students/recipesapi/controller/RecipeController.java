package com.students.recipesapi.controller;

import com.students.recipesapi.entity.Recipe;
import com.students.recipesapi.model.RecipeModel;
import com.students.recipesapi.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/get/all")
    @ResponseBody
    ResponseEntity<List<Recipe>> all() {
        List<Recipe> recipes = recipeService.findAll();
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    ResponseEntity<Recipe> one(@PathVariable Long id) {
        Recipe recipe = recipeService.findById(id);
        return ResponseEntity.ok(recipe);
    }

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    ResponseEntity<Recipe> create(@RequestBody RecipeModel recipeModel, Principal principal) {
        return ResponseEntity.ok(recipeService.create(principal.getName(), recipeModel));
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
