package com.students.recipesapi.controller;

import com.students.recipesapi.entity.Recipe;
import com.students.recipesapi.model.RecipeModel;
import com.students.recipesapi.model.RecipeResponse;
import com.students.recipesapi.service.RecipeService;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<List<RecipeResponse>> all() {
        List<RecipeResponse> recipes = recipeService
                .findAll()
                .stream()
                .map(RecipeResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    ResponseEntity<RecipeResponse> one(@PathVariable Long id) {
        RecipeResponse recipe = new RecipeResponse(recipeService.findById(id));
        return ResponseEntity.ok(recipe);
    }

    @GetMapping("/get")
    @ResponseBody
    ResponseEntity<List<RecipeResponse>> query(@RequestParam String query, @RequestParam String sort, @RequestParam String direction) {
        List<RecipeResponse> recipes = recipeService
                .findByQuerySorted(query, sort, direction)
                .stream()
                .map(RecipeResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(recipes);
    }

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    ResponseEntity<RecipeResponse> create(@RequestBody RecipeModel recipeModel, Principal principal) {
        Recipe recipe = recipeService.create(principal.getName(), recipeModel);
        RecipeResponse recipeResponse = new RecipeResponse(recipe);
        return ResponseEntity.ok(recipeResponse);
    }

    @PutMapping(value = "/update/{recipeId}", consumes = "application/json", produces = "text/plain")
    ResponseEntity<String> update(@PathVariable Long recipeId, @RequestBody RecipeModel recipeModel, Principal principal) {
        recipeModel.setId(recipeId);
        recipeService.update(principal.getName(), recipeModel);
        return ResponseEntity.ok("Successfully updated the recipe.");
    }

    @DeleteMapping(value = "/delete/{recipeId}", produces = "application/json")
    ResponseEntity<String> delete(@PathVariable Long recipeId, Principal principal) {
        recipeService.delete(principal.getName(), recipeId);
        return ResponseEntity.ok("Successfully deleted the recipe.");
    }
}
