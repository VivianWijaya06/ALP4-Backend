package com.cookeasy.prod.controller;

import com.cookeasy.prod.model.SavedRecipe;
import com.cookeasy.prod.repository.SavedRecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved")
public class SavedRecipeController {

    @Autowired
    private SavedRecipeRepository savedRecipeRepository;

    @GetMapping("/{userId}")
    public List<SavedRecipe> getSavedRecipes(@PathVariable Long userId) {
        return savedRecipeRepository.findByUserId(userId);
    }

    @PostMapping
    public SavedRecipe saveRecipe(@RequestBody SavedRecipe savedRecipe) {
        return savedRecipeRepository.save(savedRecipe);
    }
}