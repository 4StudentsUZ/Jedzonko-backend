package com.students.recipesapi.repository;

import com.students.recipesapi.entity.Rating;
import com.students.recipesapi.entity.Recipe;
import com.students.recipesapi.entity.RecipeIngredient;
import com.students.recipesapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long>, IngredientRepositoryCustom {
}

interface IngredientRepositoryCustom {
//    void deleteByRecipeId(Long recipeId);
    void deleteByRecipeId(Long recipeId);
    List<RecipeIngredient> findByRecipeId(Long recipeId);
}

//class IngredientRepositoryImpl implements IngredientRepositoryCustom {
//    @PersistenceContext
//    EntityManager entityManager;
//
//    public void deleteByRecipeId(Long recipeId) {
//        String query = "DELETE FROM RecipeIngredient r WHERE r.recipe.id = :recipeId";
//        entityManager.createQuery(query).executeUpdate();
//    }
//}
