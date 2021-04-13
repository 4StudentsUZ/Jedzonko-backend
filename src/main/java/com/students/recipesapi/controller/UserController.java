package com.students.recipesapi.controller;

import com.students.recipesapi.entity.UserEntity;
import com.students.recipesapi.exception.AlreadyExistsException;
import com.students.recipesapi.exception.InvalidInputException;
import com.students.recipesapi.exception.NotFoundException;
import com.students.recipesapi.model.RegisterModel;
import com.students.recipesapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    UserController(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping()
    @ResponseBody
    List<UserEntity> all() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    @ResponseBody
    UserEntity one(@PathVariable Long id) throws NotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d not found.", id)));
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    void register(@RequestBody RegisterModel registerModel) throws AlreadyExistsException, InvalidInputException {
        if (registerModel.getPassword() == null || registerModel.getUsername() == null || registerModel.getUsername().isEmpty() || registerModel.getPassword().isEmpty()) {
            throw new InvalidInputException("Not all necessary fields have been provided.");
        }

        registerModel.setUsername(registerModel.getUsername().trim());

        if (repository.getByUsername(registerModel.getUsername()).isPresent()) {
            throw new AlreadyExistsException(String.format("User with e-mail \"%s\" already exists.", registerModel.getUsername()));
        } else {
            if (registerModel.getPassword().length() < 8) {
                throw new InvalidInputException("The password provided is too short (less than 8 characters).");
            }
            UserEntity entity = new UserEntity();
            entity.setUsername(registerModel.getUsername());
            entity.setPassword(passwordEncoder.encode(registerModel.getPassword()));
            repository.save(entity);
        }
    }
}
