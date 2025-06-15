package com.cookeasy.prod.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cookeasy.prod.model.Recipe;
import com.cookeasy.prod.model.SaveRecipe;
import com.cookeasy.prod.model.User;
import com.cookeasy.prod.repository.SaveRecipeRepository;
import com.cookeasy.prod.repository.UserRepository;
import com.cookeasy.prod.service.RecipeService;

@RestController
@RequestMapping("/api/saved")
@CrossOrigin(origins = "*")
public class SavedRecipeController {

    @Autowired
    private SaveRecipeRepository savedRecipeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RecipeService recipeService;

    @GetMapping("/{userId}")
    public List<SaveRecipe> getSavedRecipes(@PathVariable Long userId) {
        return savedRecipeRepository.findByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<?> saveRecipe(@RequestBody Map<String, Object> request) {
        try {
            // Extract user and recipe IDs from nested request structure
            @SuppressWarnings("unchecked")
            Map<String, Object> userMap = (Map<String, Object>) request.get("user");
            @SuppressWarnings("unchecked")
            Map<String, Object> recipeMap = (Map<String, Object>) request.get("recipe");
            
            if (userMap == null || recipeMap == null) {
                return ResponseEntity.badRequest().body("User and recipe information required");
            }
            
            Long userId = Long.valueOf(userMap.get("id").toString());
            Long recipeId = Long.valueOf(recipeMap.get("id").toString());
            
            // Fetch actual entities from database
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
            Recipe recipe = recipeService.getAllRecipes().stream()
                .filter(r -> r.getId().equals(recipeId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + recipeId));
            
            // Check if this recipe is already saved by this user
            Optional<SaveRecipe> existingSave = savedRecipeRepository.findByUserIdAndRecipeId(userId, recipeId);
            if (existingSave.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Recipe already saved by this user");
            }
            
            // Create new SaveRecipe with proper entity references
            SaveRecipe savedRecipe = new SaveRecipe();
            savedRecipe.setUser(user);
            savedRecipe.setRecipe(recipe);
            
            SaveRecipe saved = savedRecipeRepository.save(savedRecipe);
            return ResponseEntity.ok(saved);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error saving recipe: " + e.getMessage());
        }
    }

    @DeleteMapping("/{savedRecipeId}")
    public ResponseEntity<?> deleteSavedRecipe(@PathVariable Long savedRecipeId) {
        try {
            // Check if the saved recipe exists
            Optional<SaveRecipe> savedRecipe = savedRecipeRepository.findById(savedRecipeId);
            if (!savedRecipe.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Delete the saved recipe
            savedRecipeRepository.deleteById(savedRecipeId);
            
            return ResponseEntity.ok().body("Saved recipe deleted successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting saved recipe: " + e.getMessage());
        }
    }
}