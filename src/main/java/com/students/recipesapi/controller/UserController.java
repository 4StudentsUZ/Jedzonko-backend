package com.students.recipesapi.controller;

import com.students.recipesapi.exception.UserNotFoundException;
import com.students.recipesapi.model.UserEntity;
import com.students.recipesapi.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository repository;

    UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping()
    List<UserEntity> all() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    UserEntity one(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
