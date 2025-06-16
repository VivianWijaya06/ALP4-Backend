package com.cookeasy.prod.model;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String difficulty;
    private int durationInMinutes;

    // New fields
    private String servings;
    private String image;

    @ElementCollection
    @CollectionTable(name = "recipe_tools", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "tool")
    private List<String> tools;

    @ElementCollection
    @CollectionTable(name = "recipe_ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "ingredient")
    private List<String> ingredients;

    @ElementCollection
    @CollectionTable(name = "recipe_steps", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "step")
    private List<String> steps;

    @ElementCollection
    @CollectionTable(name = "recipe_audio_urls", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "audio_url")
    private List<String> audioUrls;

    @Column(length = 1000)
    private String tips;

    private Boolean isUserRecipe;
}