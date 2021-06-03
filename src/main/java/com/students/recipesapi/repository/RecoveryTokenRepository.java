package com.students.recipesapi.repository;

import com.students.recipesapi.entity.RecoveryToken;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

public interface RecoveryTokenRepository extends JpaRepository<RecoveryToken, Long> {
    Optional<RecoveryToken> findRecoveryTokenByToken(String token);
}