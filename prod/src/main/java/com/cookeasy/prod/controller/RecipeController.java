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
import org.springframework.web.bind.annotation.PutMapping;
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
        try {
            System.out.println("=== Recipe Upload Started ===");
            System.out.println("Title: " + title);
            System.out.println("Description: " + description);
            System.out.println("Difficulty: " + difficulty);
            System.out.println("Duration: " + durationInMinutesStr);
            System.out.println("Servings: " + servings);
            System.out.println("Tools JSON: " + toolsJson);
            System.out.println("Ingredients JSON: " + ingredientsJson);
            System.out.println("Steps JSON: " + stepsJson);
            System.out.println("Tips: " + tips);
            System.out.println("IsUserRecipe: " + isUserRecipeStr);
            System.out.println("AudioUrls JSON: " + audioUrlsJson);
            
            Recipe recipe = new Recipe();
            recipe.setTitle(title);
            recipe.setDescription(description);
            recipe.setDifficulty(difficulty);

            // Konversi durationInMinutes ke int
            int durationInMinutes = 0;
            try {
                durationInMinutes = Integer.parseInt(durationInMinutesStr);
                System.out.println("Parsed duration: " + durationInMinutes);
            } catch (Exception e) {
                System.err.println("Error parsing duration: " + e.getMessage());
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
            System.out.println("Set isUserRecipe: " + isUserRecipe);

            // Parse JSON arrays dengan pengecekan null/empty
            try {
                System.out.println("Parsing JSON arrays...");
                if (toolsJson != null && !toolsJson.isEmpty()) {
                    recipe.setTools(new com.fasterxml.jackson.databind.ObjectMapper().readValue(toolsJson, java.util.List.class));
                    System.out.println("Parsed tools: " + recipe.getTools().size() + " items");
                } else {
                    recipe.setTools(new ArrayList<>());
                    System.out.println("Set empty tools list");
                }
                if (ingredientsJson != null && !ingredientsJson.isEmpty()) {
                    recipe.setIngredients(new com.fasterxml.jackson.databind.ObjectMapper().readValue(ingredientsJson, java.util.List.class));
                    System.out.println("Parsed ingredients: " + recipe.getIngredients().size() + " items");
                } else {
                    recipe.setIngredients(new ArrayList<>());
                    System.out.println("Set empty ingredients list");
                }
                if (stepsJson != null && !stepsJson.isEmpty()) {
                    recipe.setSteps(new com.fasterxml.jackson.databind.ObjectMapper().readValue(stepsJson, java.util.List.class));
                    System.out.println("Parsed steps: " + recipe.getSteps().size() + " items");
                } else {
                    recipe.setSteps(new ArrayList<>());
                    System.out.println("Set empty steps list");
                }
            } catch (Exception e) {
                System.err.println("Error parsing JSON: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Failed to parse JSON data (tools/ingredients/steps): " + e.getMessage() + " | toolsJson=" + toolsJson + " | ingredientsJson=" + ingredientsJson + " | stepsJson=" + stepsJson);
            }

            // Handle image upload
            System.out.println("Processing image upload...");
            if (image != null && !image.isEmpty()) {
                try {
                    String imageName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(image.getOriginalFilename());
                    Path imagePath = Paths.get("uploads/images/" + imageName);
                    Files.createDirectories(imagePath.getParent());
                    Files.write(imagePath, image.getBytes());
                    recipe.setImage("/api/files/images/" + imageName);
                    System.out.println("Image saved: " + imageName + " (URL: /api/files/images/" + imageName + ")");
                } catch (Exception e) {
                    System.err.println("Error saving image: " + e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException("Failed to store image file: " + e.getMessage());
                }
            } else {
                System.out.println("No image uploaded");
            }

            // Handle audio upload
            System.out.println("Processing audio upload...");
            java.util.List<String> audioUrls = new ArrayList<>();
            if (audio != null && audio.length > 0) {
                System.out.println("Found " + audio.length + " audio files");
                for (MultipartFile audioFile : audio) {
                    if (audioFile != null && !audioFile.isEmpty()) {
                        try {
                            String audioName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(audioFile.getOriginalFilename());
                            Path audioPath = Paths.get("uploads/audio/" + audioName);
                            Files.createDirectories(audioPath.getParent());
                            Files.write(audioPath, audioFile.getBytes());
                            audioUrls.add("/api/files/audio/" + audioName);
                            System.out.println("Audio saved: " + audioName + " (URL: /api/files/audio/" + audioName + ")");
                        } catch (Exception e) {
                            System.err.println("Error saving audio: " + e.getMessage());
                            e.printStackTrace();
                            throw new RuntimeException("Failed to store audio file: " + e.getMessage());
                        }
                    }
                }
            } else {
                System.out.println("No audio files uploaded");
            }
            
            // Tambahkan audioUrls dari input URL jika ada
            if (audioUrlsJson != null && !audioUrlsJson.isEmpty()) {
                try {
                    List<String> urlList = new com.fasterxml.jackson.databind.ObjectMapper().readValue(audioUrlsJson, java.util.List.class);
                    audioUrls.addAll(urlList);
                    System.out.println("Added " + urlList.size() + " audio URLs");
                } catch (Exception e) {
                    System.err.println("Error parsing audio URLs: " + e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException("Failed to parse audioUrls JSON: " + e.getMessage());
                }
            }
            // Selalu set audioUrls (meskipun kosong)
            recipe.setAudioUrls(audioUrls);
            System.out.println("Total audio URLs: " + audioUrls.size());

            System.out.println("Saving recipe to database...");
            Recipe savedRecipe = recipeService.saveRecipe(recipe);
            System.out.println("Recipe saved successfully with ID: " + savedRecipe.getId());
            System.out.println("=== Recipe Upload Completed ===");
            return savedRecipe;
        } catch (Exception ex) {
            System.err.println("=== Recipe Upload Failed ===");
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Gagal menyimpan resep ke database: " + ex.getMessage());
        }
    }

    @PutMapping("/fix-image-urls")
    public String fixImageUrls() {
        try {
            List<Recipe> allRecipes = recipeService.getAllRecipes();
            int fixedImageCount = 0;
            int fixedAudioCount = 0;
            
            for (Recipe recipe : allRecipes) {
                boolean needsSave = false;
                
                // Fix image URLs
                if (recipe.getImage() != null && recipe.getImage().startsWith("/uploads/images/")) {
                    String correctedUrl = recipe.getImage().replace("/uploads/images/", "/api/files/images/");
                    recipe.setImage(correctedUrl);
                    fixedImageCount++;
                    needsSave = true;
                    System.out.println("Fixed recipe " + recipe.getId() + " image: " + correctedUrl);
                }
                
                // Fix audio URLs
                if (recipe.getAudioUrls() != null && !recipe.getAudioUrls().isEmpty()) {
                    List<String> correctedAudioUrls = new ArrayList<>();
                    boolean audioFixed = false;
                    
                    for (String audioUrl : recipe.getAudioUrls()) {
                        if (audioUrl != null && audioUrl.startsWith("/uploads/audio/")) {
                            String correctedAudioUrl = audioUrl.replace("/uploads/audio/", "/api/files/audio/");
                            correctedAudioUrls.add(correctedAudioUrl);
                            audioFixed = true;
                            System.out.println("Fixed recipe " + recipe.getId() + " audio: " + correctedAudioUrl);
                        } else {
                            correctedAudioUrls.add(audioUrl);
                        }
                    }
                    
                    if (audioFixed) {
                        recipe.setAudioUrls(correctedAudioUrls);
                        fixedAudioCount++;
                        needsSave = true;
                    }
                }
                
                if (needsSave) {
                    recipeService.saveRecipe(recipe);
                }
            }
            
            return "Fixed " + fixedImageCount + " recipe image URLs and " + fixedAudioCount + " recipe audio URLs";
        } catch (Exception e) {
            System.err.println("Error fixing URLs: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}