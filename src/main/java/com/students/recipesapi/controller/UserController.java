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
    List<UserEntity> all() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseBody
    UserEntity one(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    void register(@RequestBody RegisterModel registerModel) throws AlreadyExistsException, InvalidInputException {
        userService.register(registerModel);
    }
}
