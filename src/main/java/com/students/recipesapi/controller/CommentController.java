package com.students.recipesapi.controller;

import com.students.recipesapi.entity.Comment;
import com.students.recipesapi.entity.Recipe;
import com.students.recipesapi.model.CommentModel;
import com.students.recipesapi.model.RecipeModel;
import com.students.recipesapi.service.CommentService;
import com.students.recipesapi.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/get/forRecipe/{recipeId}")
    @ResponseBody
    ResponseEntity<List<Comment>> forRecipe(@PathVariable Long recipeId) {
        return ResponseEntity.ok(commentService.findForRecipe(recipeId));
    }

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    ResponseEntity<Comment> create(@RequestBody CommentModel commentModel, Principal principal) {
        return ResponseEntity.ok(commentService.create(principal.getName(), commentModel));
    }

    @PostMapping(value = "/update", consumes = "application/json", produces = "application/json")
    ResponseEntity<Comment> update(@RequestBody CommentModel commentModel, Principal principal) {
        return ResponseEntity.ok(commentService.update(principal.getName(), commentModel));
    }

    @DeleteMapping(value = "/delete/{commentId}")
    void delete(@PathVariable Long commentId, Principal principal) {
        commentService.delete(principal.getName(), commentId);
    }
}
