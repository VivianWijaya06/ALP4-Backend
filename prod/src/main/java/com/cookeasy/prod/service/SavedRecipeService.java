package com.cookeasy.prod.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cookeasy.prod.model.Recipe;
import com.cookeasy.prod.model.SavedRecipe;
import com.cookeasy.prod.model.User;
import com.cookeasy.prod.repository.RecipeRepository;
import com.cookeasy.prod.repository.SavedRecipeRepository;
import com.cookeasy.prod.repository.UserRepository;

@Service
@Transactional
public class SavedRecipeService {
    
    @Autowired
    private SavedRecipeRepository savedRecipeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    public SavedRecipe saveRecipe(Long userId, Long recipeId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));
            
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new IllegalArgumentException("Recipe tidak ditemukan"));
            
        // Check if already saved
        Optional<SavedRecipe> existing = savedRecipeRepository.findByUserIdAndRecipeId(userId, recipeId);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Recipe sudah disimpan sebelumnya");
        }
        
        SavedRecipe savedRecipe = new SavedRecipe();
        savedRecipe.setUser(user);
        savedRecipe.setRecipe(recipe);
        
        return savedRecipeRepository.save(savedRecipe);
    }
    
    public void unsaveRecipe(Long userId, Long recipeId) {
        savedRecipeRepository.deleteByUserIdAndRecipeId(userId, recipeId);
    }
    
    public List<SavedRecipe> getSavedRecipesByUser(Long userId) {
        return savedRecipeRepository.findByUserId(userId);
    }
    
    public boolean isRecipeSavedByUser(Long userId, Long recipeId) {
        return savedRecipeRepository.findByUserIdAndRecipeId(userId, recipeId).isPresent();
    }
}
