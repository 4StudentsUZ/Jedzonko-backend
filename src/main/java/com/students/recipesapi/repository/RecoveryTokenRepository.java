package com.students.recipesapi.repository;

import com.students.recipesapi.entity.RecoveryToken;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

public interface RecoveryTokenRepository extends JpaRepository<RecoveryToken, Long>, RecoveryTokenRepositoryCustom {
}

interface RecoveryTokenRepositoryCustom {
    Optional<RecoveryToken> findByToken(String token);
}

class RecoveryTokenRepositoryImplementation implements RecoveryTokenRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    public Optional<RecoveryToken> findByToken(String token) {
        String query = "SELECT u FROM RecoveryToken u WHERE u.token LIKE :token";
        try {
            return Optional.ofNullable(
                    (RecoveryToken) entityManager
                            .createQuery(query)
                            .setParameter("token", token)
                            .getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
