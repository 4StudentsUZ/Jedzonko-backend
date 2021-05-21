package com.students.recipesapi.repository;

import com.students.recipesapi.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query(value = "SELECT * FROM Recipe r WHERE r.title ILIKE %:query%", nativeQuery = true)
    List<Recipe> findAllByQuery(@Param("query") String query);
}