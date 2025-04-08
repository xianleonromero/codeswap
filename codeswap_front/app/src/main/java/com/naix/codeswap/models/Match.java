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

    // Getters y Setters
    // ...

    public boolean isPotentialMatch() {
        return TYPE_POTENTIAL.equals(matchType);
    }
}