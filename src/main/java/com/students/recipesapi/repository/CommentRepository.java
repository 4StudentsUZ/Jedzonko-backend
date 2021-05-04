package com.students.recipesapi.repository;

import com.students.recipesapi.entity.Comment;
import com.students.recipesapi.entity.Product;
import com.students.recipesapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
}


interface CommentRepositoryCustom {
    @Transactional
    List<Comment> findForRecipe(Long recipeId);
}

class CommentRepositoryImpl implements CommentRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    public List<Comment> findForRecipe(Long recipeId) {
        String query = "SELECT c FROM Comment c WHERE c.recipe.id = :recipeId";
        return entityManager
            .createQuery(query)
            .setParameter("recipeId", recipeId)
            .getResultList();
   }
}