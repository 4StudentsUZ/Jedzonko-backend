package com.students.recipesapi.repository;

import com.students.recipesapi.entity.Rating;
import com.students.recipesapi.entity.Recipe;
import com.students.recipesapi.entity.RecoveryToken;
import com.students.recipesapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long>, RatingRepositoryCustom {
}


interface RatingRepositoryCustom {
    Optional<Rating> findByUserAndRecipe(UserEntity user, Recipe recipe);
    double getAvgForRecipe(Recipe recipe);
}

class RatingRepositoryImpl implements RatingRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    public Optional<Rating> findByUserAndRecipe(UserEntity user, Recipe recipe) {
        String query = "SELECT r FROM Rating r WHERE r.user.id = :userId AND r.recipe.id = :recipeId";
        try {
            return Optional.ofNullable(
                    (Rating) entityManager
                            .createQuery(query)
                            .setParameter("userId", user.getId())
                            .setParameter("recipeId", recipe.getId())
                            .getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public double getAvgForRecipe(Recipe recipe) {
        String query = "SELECT AVG(r.rating) FROM Rating r WHERE r.recipe.id = :recipeId";
        return (double) entityManager
                .createQuery(query)
                .setParameter("recipeId", recipe.getId())
                .getSingleResult();
    }
}