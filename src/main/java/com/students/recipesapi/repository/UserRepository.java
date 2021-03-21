package com.students.recipesapi.repository;

import com.students.recipesapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
}

interface UserRepositoryCustom {
    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByUsername(String username);
}

class UserRepositoryImpl implements UserRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    public Optional<User> getUserByEmail(String email) {
        String query = "SELECT u FROM User u WHERE u.email LIKE :email";
        try {
            return Optional.ofNullable(
                    (User) entityManager
                            .createQuery(query)
                            .setParameter("email", email)
                            .getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<User> getUserByUsername(String username) {
        String query = "SELECT u FROM User u WHERE u.username LIKE :username";
        try {
            return Optional.ofNullable(
                    (User) entityManager
                            .createQuery(query)
                            .setParameter("username", username)
                            .getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
