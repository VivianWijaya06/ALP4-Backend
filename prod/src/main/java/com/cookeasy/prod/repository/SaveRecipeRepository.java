package com.cookeasy.prod.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cookeasy.prod.model.SaveRecipe;

public interface SaveRecipeRepository extends JpaRepository<SaveRecipe, Long> {
    List<SaveRecipe> findByUserId(Long userId);
    
    @Query("SELECT s FROM SaveRecipe s WHERE s.user.id = :userId AND s.recipe.id = :recipeId")
    Optional<SaveRecipe> findByUserIdAndRecipeId(@Param("userId") Long userId, @Param("recipeId") Long recipeId);
}