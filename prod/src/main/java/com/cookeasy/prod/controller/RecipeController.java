package com.cookeasy.prod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cookeasy.prod.model.Recipe;
import com.cookeasy.prod.repository.RecipeRepository;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "*")
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping
    public List<Recipe> getRecipes(@RequestParam(value = "title", required = false) String title) {
        if (title != null && !title.isEmpty()) {
            return recipeRepository.findByTitleContainingIgnoreCase(title);
        }
        return recipeRepository.findAll();
    }

    @PostMapping
    public Recipe createRecipe(@RequestBody Recipe recipe) {
        System.out.println("Menerima request resep baru: " + recipe);
        Recipe savedRecipe = recipeRepository.save(recipe);
        System.out.println("Resep berhasil disimpan dengan ID: " + savedRecipe.getId());
        return savedRecipe;
    }
}