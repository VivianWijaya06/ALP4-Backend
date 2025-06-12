package com.cookeasy.prod.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cookeasy.prod.model.SaveRecipe;

public interface SaveRecipeRepository extends JpaRepository<SaveRecipe, Long> {
    List<SaveRecipe> findByUserId(Long userId);
}