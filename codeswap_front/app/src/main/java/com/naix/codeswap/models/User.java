package com.naix.codeswap.models;

import java.util.List;

public class User {
    public User(){}
    private int id;
    private String username;
    private String fullName;
    private String email;
    private String bio;
    private String avatarUrl;
    private float rating;
    private List<OfferedSkill> offeredSkills;
    private List<WantedSkill> wantedSkills;

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getBio() {
        return bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public float getRating() {
        return rating;
    }

    public List<OfferedSkill> getOfferedSkills() {
        return offeredSkills;
    }

    public List<WantedSkill> getWantedSkills() {
        return wantedSkills;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setOfferedSkills(List<OfferedSkill> offeredSkills) {
        this.offeredSkills = offeredSkills;
    }

    public void setWantedSkills(List<WantedSkill> wantedSkills) {
        this.wantedSkills = wantedSkills;
    }
}