package com.students.recipesapi;

import com.students.recipesapi.model.UserEntity;
import com.students.recipesapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> log.info("Preloading " + userRepository.save(new UserEntity("jkow", "Jan", "Kowalski", "jan.kowalski@gmail.com", "$2b$10$skJlwF3pkXBslldjZtIkmOdUJcImTbeWlweqZsJ7FoscY3shfx4Wq")));
    }
}
