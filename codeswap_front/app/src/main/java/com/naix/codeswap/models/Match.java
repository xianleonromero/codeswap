package com.naix.codeswap.models;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Date;
import java.util.List;

public class Match {
    public Match(){
    }
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
    private List<ProgrammingLanguage> user2Offers;


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

    // Método estático para crear Match desde Map (JSON)
    public static Match fromMap(Map<String, Object> data) {
        Match match = new Match();

        if (data.containsKey("id")) {
            match.setId(((Double) data.get("id")).intValue());
        }

        if (data.containsKey("match_type")) {
            match.setMatchType((String) data.get("match_type"));
        }

        if (data.containsKey("compatibility_score")) {
            Object score = data.get("compatibility_score");
            if (score instanceof Double) {
                match.setCompatibilityScore(((Double) score).floatValue());
            } else if (score instanceof Float) {
                match.setCompatibilityScore((Float) score);
            }
        }

        if (data.containsKey("created_at")) {
            // Aquí podrías parsear la fecha si es necesario
            match.setCreatedAt(new Date());
        }

        // Parsear user1
        if (data.containsKey("user1")) {
            Map<String, Object> user1Data = (Map<String, Object>) data.get("user1");
            User user1 = new User();
            user1.setId(((Double) user1Data.get("id")).intValue());
            user1.setUsername((String) user1Data.get("username"));
            user1.setFullName((String) user1Data.get("first_name") + " " + (String) user1Data.get("last_name"));
            match.setUser1(user1);
        }

        // Parsear user2
        if (data.containsKey("user2")) {
            Map<String, Object> user2Data = (Map<String, Object>) data.get("user2");
            User user2 = new User();
            user2.setId(((Double) user2Data.get("id")).intValue());
            user2.setUsername((String) user2Data.get("username"));
            user2.setFullName((String) user2Data.get("first_name") + " " + (String) user2Data.get("last_name"));
            match.setUser2(user2);
        }

        // Parsear user1_offers
        if (data.containsKey("user1_offers")) {
            List<Map<String, Object>> offersData = (List<Map<String, Object>>) data.get("user1_offers");
            List<ProgrammingLanguage> offers = new ArrayList<>();
            for (Map<String, Object> langData : offersData) {
                ProgrammingLanguage lang = new ProgrammingLanguage();
                lang.setId(((Double) langData.get("id")).intValue());
                lang.setName((String) langData.get("name"));
                offers.add(lang);
            }
            match.setUser1Offers(offers);
        }

        // Parsear user2_wants
        if (data.containsKey("user2_wants")) {
            List<Map<String, Object>> wantsData = (List<Map<String, Object>>) data.get("user2_wants");
            List<ProgrammingLanguage> wants = new ArrayList<>();
            for (Map<String, Object> langData : wantsData) {
                ProgrammingLanguage lang = new ProgrammingLanguage();
                lang.setId(((Double) langData.get("id")).intValue());
                lang.setName((String) langData.get("name"));
                wants.add(lang);
            }
            match.setUser2Wants(wants);
        }
        if (data.containsKey("user2_offers")) {
            List<Map<String, Object>> offersData = (List<Map<String, Object>>) data.get("user2_offers");
            List<ProgrammingLanguage> user2Offers = new ArrayList<>();

            for (Map<String, Object> offerData : offersData) {
                ProgrammingLanguage lang = new ProgrammingLanguage();
                lang.setId(((Double) offerData.get("id")).intValue());
                lang.setName((String) offerData.get("name"));
                if (offerData.containsKey("icon")) {
                    lang.setIcon((String) offerData.get("icon"));
                }
                user2Offers.add(lang);
            }
            match.setUser2Offers(user2Offers);
        }
        return match;
    }

    public List<ProgrammingLanguage> getUser2Offers() {
        return user2Offers != null ? user2Offers : new ArrayList<>();
    }

    public void setUser2Offers(List<ProgrammingLanguage> user2Offers) {
        this.user2Offers = user2Offers;
    }
}