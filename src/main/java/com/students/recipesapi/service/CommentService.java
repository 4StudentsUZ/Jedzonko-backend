package com.students.recipesapi.service;

import com.students.recipesapi.entity.Comment;
import com.students.recipesapi.entity.Recipe;
import com.students.recipesapi.entity.UserEntity;
import com.students.recipesapi.exception.InvalidInputException;
import com.students.recipesapi.exception.NotFoundException;
import com.students.recipesapi.model.CommentModel;
import com.students.recipesapi.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final RecipeService recipeService;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, RecipeService recipeService, UserService userService) {
        this.commentRepository = commentRepository;
        this.recipeService = recipeService;
        this.userService = userService;
    }

    public List<Comment> findForRecipe(Long recipeId) {
        return commentRepository.findForRecipe(recipeId);
    }

    public Comment findById(Long commentId) {
        return commentRepository
                .findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id %d not found.", commentId)));
    }

    public Comment create(String username, CommentModel commentModel) {
        validateCommentForCreate(commentModel);
        Recipe recipe = recipeService.findById(commentModel.recipeId);
        UserEntity author = userService.findByUsername(username);

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setContent(commentModel.getContent());
        comment.setRecipe(recipe);
        comment.setCreationDate(LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString());

        return commentRepository.save(comment);
    }

    public Comment update(String username, CommentModel commentModel) {
        validateCommentForUpdate(commentModel);
        Comment comment = findById(commentModel.commentId);
        validateAuthority(username, comment);

        comment.setContent(commentModel.getContent());
        comment.setModificationDate(LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString());

        return commentRepository.save(comment);
    }

    public void delete(String username, Long commentId) {
        Comment comment = findById(commentId);
        validateAuthority(username, comment);
        commentRepository.delete(comment);
    }

    private void validateCommentForCreate(CommentModel commentModel) {
        if (commentModel.content == null || commentModel.content.isEmpty()) {
            throw new InvalidInputException("Comment's content was not provided.");
        }
        if (commentModel.getRecipeId() == null) {
            throw new InvalidInputException("Comment's recipe id was not provided.");
        }
    }

    private void validateCommentForUpdate(CommentModel commentModel) {
        if (commentModel.content == null || commentModel.content.isEmpty()) {
            throw new InvalidInputException("Comment's content was not provided.");
        }
        if (commentModel.getCommentId() == null) {
            throw new InvalidInputException("Comment's id was not provided.");
        }
    }

    private void validateAuthority(String authorUsername, Comment comment) {
        if (!authorUsername.equals(comment.getAuthor().getUsername())) {
            throw new InvalidInputException("Tried to act on a recipe the user has was not authorized to.");
        }
    }
}
