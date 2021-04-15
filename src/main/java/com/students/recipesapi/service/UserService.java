package com.students.recipesapi.service;

import com.students.recipesapi.entity.UserEntity;
import com.students.recipesapi.exception.AlreadyExistsException;
import com.students.recipesapi.exception.InvalidInputException;
import com.students.recipesapi.exception.NotFoundException;
import com.students.recipesapi.model.RegisterModel;
import com.students.recipesapi.model.UserUpdateModel;
import com.students.recipesapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        if (registerModel.getUsername() == null || registerModel.getUsername().isEmpty()) {
            throw new InvalidInputException("Haven't provided a valid username.");
        }
        validatePassword(registerModel.getPassword());

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

    public UserEntity update(String username, UserUpdateModel userUpdateModel) {
        if (username == null || username.isEmpty()) {
            throw new InvalidInputException("Tried to update user without a username.");
        }

        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);
        if (!userEntityOptional.isPresent()) {
            throw new NotFoundException(String.format("User with username \"%s\" not found.", username));
        }

        UserEntity userEntity = userEntityOptional.get();
        if (userUpdateModel.getFirstName() != null) userEntity.setFirstName(userUpdateModel.getFirstName());
        if (userUpdateModel.getLastName() != null) userEntity.setLastName(userUpdateModel.getLastName());
        if (userUpdateModel.getPassword() != null) {
            validatePassword(userUpdateModel.getPassword());
            userEntity.setPassword(passwordEncoder.encode(userUpdateModel.getPassword()));
        }
        userRepository.save(userEntity);

        return userEntity;
    }

    public void validatePassword(String password) {
        if (password == null) {
            throw new InvalidInputException("Password haven't been provided.");
        }
        if (password.length() < 8) {
            throw new InvalidInputException("Provided password is too short.");
        }
    }
}
