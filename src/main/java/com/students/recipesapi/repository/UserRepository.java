package com.students.recipesapi.repository;

import com.students.recipesapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long>, UserRepositoryCustom {
}

interface UserRepositoryCustom {
    Optional<UserEntity> getByEmail(String email);

    Optional<UserEntity> getByUsername(String username);
}

class UserRepositoryImpl implements UserRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    public Optional<UserEntity> getByEmail(String email) {
        String query = "SELECT u FROM UserEntity u WHERE u.email LIKE :email";
        try {
            return Optional.ofNullable(
                    (UserEntity) entityManager
                            .createQuery(query)
                            .setParameter("email", email)
                            .getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<UserEntity> getByUsername(String username) {
        String query = "SELECT u FROM UserEntity u WHERE u.username LIKE :username";
        try {
            return Optional.ofNullable(
                    (UserEntity) entityManager
                            .createQuery(query)
                            .setParameter("username", username)
                            .getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
