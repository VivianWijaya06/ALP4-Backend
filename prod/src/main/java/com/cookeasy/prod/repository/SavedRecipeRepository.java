package com.cookeasy.prod.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cookeasy.prod.model.SavedRecipe;

@Repository
public interface SavedRecipeRepository extends JpaRepository<SavedRecipe, Long> {
    
    @Query("SELECT sr FROM SavedRecipe sr WHERE sr.user.id = :userId")
    List<SavedRecipe> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT sr FROM SavedRecipe sr WHERE sr.user.id = :userId AND sr.recipe.id = :recipeId")
    Optional<SavedRecipe> findByUserIdAndRecipeId(@Param("userId") Long userId, @Param("recipeId") Long recipeId);
    
    @Query("DELETE FROM SavedRecipe sr WHERE sr.user.id = :userId AND sr.recipe.id = :recipeId")
    void deleteByUserIdAndRecipeId(@Param("userId") Long userId, @Param("recipeId") Long recipeId);
}
