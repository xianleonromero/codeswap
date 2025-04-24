package com.naix.codeswap.models;

import java.util.Date;
import java.util.List;

public class Match {
    public static final String TYPE_POTENTIAL = "POTENTIAL";
    public static final String TYPE_NORMAL = "NORMAL";

    private int id;
    private User user1;
    private User user2;
    private String matchType;
    private Date createdAt;
    private float compatibilityScore;
    private List<ProgrammingLanguage> user1Offers;
    private List<ProgrammingLanguage> user2Wants;

// En Match.java - Añade estos getters y setters si no los tienes

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public float getCompatibilityScore() {
        return compatibilityScore;
    }

    public void setCompatibilityScore(float compatibilityScore) {
        this.compatibilityScore = compatibilityScore;
    }

    public List<ProgrammingLanguage> getUser1Offers() {
        return user1Offers;
    }

    public void setUser1Offers(List<ProgrammingLanguage> user1Offers) {
        this.user1Offers = user1Offers;
    }

    public List<ProgrammingLanguage> getUser2Wants() {
        return user2Wants;
    }

    public void setUser2Wants(List<ProgrammingLanguage> user2Wants) {
        this.user2Wants = user2Wants;
    }

    public boolean isPotentialMatch() {
        return TYPE_POTENTIAL.equals(matchType);
    }
}