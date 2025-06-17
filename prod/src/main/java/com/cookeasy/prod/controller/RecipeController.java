package com.cookeasy.prod.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cookeasy.prod.model.Recipe;
import com.cookeasy.prod.service.RecipeService;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "*")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping
    public List<Recipe> getRecipes(@RequestParam(value = "title", required = false) String title) {
        if (title != null && !title.isEmpty()) {
            return recipeService.findByTitleContainingIgnoreCase(title);
        }
        return recipeService.getAllRecipes();
    }

    @PostMapping
    public Recipe createRecipe(@RequestBody Recipe recipe) {
        System.out.println("Menerima request resep baru: " + recipe);
        Recipe savedRecipe = recipeService.saveRecipe(recipe);
        System.out.println("Resep berhasil disimpan dengan ID: " + savedRecipe.getId());
        return savedRecipe;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Recipe uploadRecipe(
        @RequestPart("title") String title,
        @RequestPart("description") String description,
        @RequestPart("difficulty") String difficulty,
        @RequestPart("durationInMinutes") String durationInMinutesStr,
        @RequestPart("servings") String servings,
        @RequestPart(value = "image", required = false) MultipartFile image,
        @RequestPart(value = "audio", required = false) MultipartFile[] audio,
        @RequestPart("tools") String toolsJson,
        @RequestPart("ingredients") String ingredientsJson,
        @RequestPart("steps") String stepsJson,
        @RequestPart(value = "tips", required = false) String tips,
        @RequestPart(value = "isUserRecipe", required = false) String isUserRecipeStr,
        @RequestPart(value = "audioUrls", required = false) String audioUrlsJson
    ) {
        Recipe recipe = new Recipe();
        recipe.setTitle(title);
        recipe.setDescription(description);
        recipe.setDifficulty(difficulty);

        // Konversi durationInMinutes ke int
        int durationInMinutes = 0;
        try {
            durationInMinutes = Integer.parseInt(durationInMinutesStr);
        } catch (Exception e) {
            throw new RuntimeException("durationInMinutes harus berupa angka: " + durationInMinutesStr);
        }
        recipe.setDurationInMinutes(durationInMinutes);

        recipe.setServings(servings);

        // Pastikan tips tidak null
        recipe.setTips(tips != null ? tips : "");

        // Pastikan isUserRecipe tidak null
        boolean isUserRecipe = true;
        if (isUserRecipeStr != null) {
            isUserRecipe = Boolean.parseBoolean(isUserRecipeStr);
        }
        recipe.setIsUserRecipe(isUserRecipe);

        // Parse JSON arrays dengan pengecekan null/empty
        try {
            if (toolsJson != null && !toolsJson.isEmpty()) {
                recipe.setTools(new com.fasterxml.jackson.databind.ObjectMapper().readValue(toolsJson, java.util.List.class));
            } else {
                recipe.setTools(new ArrayList<>());
            }
            if (ingredientsJson != null && !ingredientsJson.isEmpty()) {
                recipe.setIngredients(new com.fasterxml.jackson.databind.ObjectMapper().readValue(ingredientsJson, java.util.List.class));
            } else {
                recipe.setIngredients(new ArrayList<>());
            }
            if (stepsJson != null && !stepsJson.isEmpty()) {
                recipe.setSteps(new com.fasterxml.jackson.databind.ObjectMapper().readValue(stepsJson, java.util.List.class));
            } else {
                recipe.setSteps(new ArrayList<>());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse JSON data (tools/ingredients/steps): " + e.getMessage() + " | toolsJson=" + toolsJson + " | ingredientsJson=" + ingredientsJson + " | stepsJson=" + stepsJson);
        }

        // Handle image upload
        if (image != null && !image.isEmpty()) {
            try {
                String imageName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(image.getOriginalFilename());
                Path imagePath = Paths.get("uploads/images/" + imageName);
                Files.createDirectories(imagePath.getParent());
                Files.write(imagePath, image.getBytes());
                recipe.setImage("/uploads/images/" + imageName);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to store image file: " + e.getMessage());
            }
        }

        // Handle audio upload
        java.util.List<String> audioUrls = new ArrayList<>();
        if (audio != null && audio.length > 0) {
            for (MultipartFile audioFile : audio) {
                if (audioFile != null && !audioFile.isEmpty()) {
                    try {
                        String audioName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(audioFile.getOriginalFilename());
                        Path audioPath = Paths.get("uploads/audio/" + audioName);
                        Files.createDirectories(audioPath.getParent());
                        Files.write(audioPath, audioFile.getBytes());
                        audioUrls.add("/uploads/audio/" + audioName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Failed to store audio file: " + e.getMessage());
                    }
                }
            }
        }
        // Tambahkan audioUrls dari input URL jika ada
        if (audioUrlsJson != null && !audioUrlsJson.isEmpty()) {
            try {
                List<String> urlList = new com.fasterxml.jackson.databind.ObjectMapper().readValue(audioUrlsJson, java.util.List.class);
                audioUrls.addAll(urlList);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to parse audioUrls JSON: " + e.getMessage());
            }
        }
        // Selalu set audioUrls (meskipun kosong)
        recipe.setAudioUrls(audioUrls);

        try {
            Recipe savedRecipe = recipeService.saveRecipe(recipe);
            System.out.println("Resep berhasil disimpan dengan ID: " + savedRecipe.getId());
            return savedRecipe;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Gagal menyimpan resep ke database: " + ex.getMessage());
        }
    }
}