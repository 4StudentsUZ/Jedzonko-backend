package com.students.recipesapi.service;

import com.students.recipesapi.entity.UserEntity;
import com.students.recipesapi.exception.AlreadyExistsException;
import com.students.recipesapi.exception.InvalidInputException;
import com.students.recipesapi.exception.NotFoundException;
import com.students.recipesapi.model.RegisterModel;
import com.students.recipesapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public UserEntity findById(long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d not found.", userId)));
    }

    public UserEntity register(RegisterModel registerModel) {
        if (registerModel.getPassword() == null || registerModel.getUsername() == null || registerModel.getUsername().isEmpty() || registerModel.getPassword().isEmpty()) {
            throw new InvalidInputException("Not all necessary fields have been provided.");
        }

        registerModel.setUsername(registerModel.getUsername().trim());

        if (userRepository.existsByUsername(registerModel.getUsername())) {
            throw new AlreadyExistsException(String.format("User with e-mail \"%s\" already exists.", registerModel.getUsername()));
        }
        if (registerModel.getPassword().length() < 8) {
            throw new InvalidInputException("The password provided is too short (less than 8 characters).");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(registerModel.getUsername());
        userEntity.setPassword(passwordEncoder.encode(registerModel.getPassword()));
        userRepository.save(userEntity);

        return userEntity;
    }
}
