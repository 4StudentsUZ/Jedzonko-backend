package com.students.recipesapi.controller;

import com.students.recipesapi.entity.UserEntity;
import com.students.recipesapi.exception.AlreadyExistsException;
import com.students.recipesapi.exception.InvalidInputException;
import com.students.recipesapi.exception.NotFoundException;
import com.students.recipesapi.model.RegisterModel;
import com.students.recipesapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    @ResponseBody
    ResponseEntity<List<UserEntity>> all() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    @ResponseBody
    ResponseEntity<UserEntity> one(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    ResponseEntity<UserEntity> register(@RequestBody RegisterModel registerModel) throws AlreadyExistsException, InvalidInputException {
        return ResponseEntity.ok(userService.register(registerModel));
    }
}
