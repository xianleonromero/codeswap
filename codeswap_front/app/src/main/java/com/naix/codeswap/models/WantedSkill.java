package com.naix.codeswap.models;

public class WantedSkill {
    private int id;
    private User user;
    private ProgrammingLanguage language;

    // Getters
    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public ProgrammingLanguage getLanguage() {
        return language;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLanguage(ProgrammingLanguage language) {
        this.language = language;
    }
}