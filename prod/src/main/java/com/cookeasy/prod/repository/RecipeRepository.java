package com.cookeasy.prod.repository;

import com.cookeasy.prod.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}