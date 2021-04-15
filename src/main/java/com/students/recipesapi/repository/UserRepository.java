package com.students.recipesapi.repository;

import com.students.recipesapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long>, UserRepositoryCustom {
}

interface UserRepositoryCustom {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}

class UserRepositoryImpl implements UserRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    public Optional<UserEntity> findByUsername(String username) {
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

    public boolean existsByUsername(String username) {
        String query = "SELECT u FROM UserEntity u WHERE u.username LIKE :username";
        return entityManager
                .createQuery(query)
                .setParameter("username", username)
                .getResultList().size() != 0;
    }
}
