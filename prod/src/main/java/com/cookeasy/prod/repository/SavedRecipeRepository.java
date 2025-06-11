package com.cookeasy.prod.repository;

import com.cookeasy.prod.model.SavedRecipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedRecipeRepository extends JpaRepository<SavedRecipe, Long> {
    List<SavedRecipe> findByUserId(Long userId);
}