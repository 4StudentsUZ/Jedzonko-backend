package com.students.recipesapi.controller;

import com.students.recipesapi.entity.UserEntity;
import com.students.recipesapi.model.RegisterModel;
import com.students.recipesapi.model.UserUpdateModel;
import com.students.recipesapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
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
    ResponseEntity<UserEntity> register(@RequestBody RegisterModel registerModel) {
        return ResponseEntity.ok(userService.register(registerModel));
    }

    @PutMapping(value = "/update", consumes = "application/json", produces = "application/json")
    ResponseEntity<UserEntity> update(@RequestBody UserUpdateModel userUpdateModel) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(userService.update(username, userUpdateModel));
    }
}
