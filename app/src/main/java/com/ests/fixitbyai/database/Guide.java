package com.ests.fixitbyai.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "guides")
public class Guide {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private String category;
    private String difficulty;
    private String steps;
    private String imageUrl;

    public Guide(String title, String description, String category, String difficulty, String steps, String imageUrl) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.steps = steps;
        this.imageUrl = imageUrl;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
} 