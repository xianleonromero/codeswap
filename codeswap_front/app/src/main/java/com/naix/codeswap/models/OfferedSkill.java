package com.naix.codeswap.models;

public class OfferedSkill {
    private int id;
    private User user;
    private ProgrammingLanguage language;
    private int level; // 1-5 por ejemplo

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

    public int getLevel() {
        return level;
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

    public void setLevel(int level) {
        this.level = level;
    }
}