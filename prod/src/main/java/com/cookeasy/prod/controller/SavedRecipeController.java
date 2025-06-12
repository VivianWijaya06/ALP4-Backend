package com.cookeasy.prod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cookeasy.prod.model.SaveRecipe;
import com.cookeasy.prod.repository.SaveRecipeRepository;

@RestController
@RequestMapping("/api/saved_recipes")
public class SavedRecipeController {

    @Autowired
    private SaveRecipeRepository savedRecipeRepository;

    @GetMapping("/{userId}")
    public List<SaveRecipe> getSavedRecipes(@PathVariable Long userId) {
        return savedRecipeRepository.findByUserId(userId);
    }

    @PostMapping
    public SaveRecipe saveRecipe(@RequestBody SaveRecipe savedRecipe) {
        return savedRecipeRepository.save(savedRecipe);
    }
}