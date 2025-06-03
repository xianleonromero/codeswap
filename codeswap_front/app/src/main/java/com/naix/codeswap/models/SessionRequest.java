package com.naix.codeswap.models;

import java.util.Date;
import java.util.Map;

public class SessionRequest {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_REJECTED = "REJECTED";

    private int id;
    private User requester;
    private ProgrammingLanguage language;
    private String message;
    private String status;
    private Date proposedDateTime;
    private int durationMinutes;
    private Date createdAt;

    public SessionRequest() {}

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public ProgrammingLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ProgrammingLanguage language) {
        this.language = language;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getProposedDateTime() {
        return proposedDateTime;
    }

    public void setProposedDateTime(Date proposedDateTime) {
        this.proposedDateTime = proposedDateTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }

    public boolean isAccepted() {
        return STATUS_ACCEPTED.equals(status);
    }

    public boolean isRejected() {
        return STATUS_REJECTED.equals(status);
    }

    // Método para crear desde Map (JSON)
    public static SessionRequest fromMap(Map<String, Object> data) {
        SessionRequest request = new SessionRequest();

        if (data.containsKey("id")) {
            request.setId(((Double) data.get("id")).intValue());
        }

        if (data.containsKey("status")) {
            request.setStatus((String) data.get("status"));
        }

        if (data.containsKey("message")) {
            request.setMessage((String) data.get("message"));
        }

        if (data.containsKey("duration_minutes")) {
            request.setDurationMinutes(((Double) data.get("duration_minutes")).intValue());
        }

        // Parsear requester
        if (data.containsKey("requester")) {
            Map<String, Object> requesterData = (Map<String, Object>) data.get("requester");
            User requester = new User();
            requester.setId(((Double) requesterData.get("id")).intValue());
            requester.setUsername((String) requesterData.get("username"));

            String firstName = (String) requesterData.get("first_name");
            String lastName = (String) requesterData.get("last_name");
            requester.setFullName((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));

            request.setRequester(requester);
        }

        // Parsear language
        if (data.containsKey("language")) {
            Map<String, Object> languageData = (Map<String, Object>) data.get("language");
            ProgrammingLanguage language = new ProgrammingLanguage();
            language.setId(((Double) languageData.get("id")).intValue());
            language.setName((String) languageData.get("name"));
            if (languageData.containsKey("icon")) {
                language.setIcon((String) languageData.get("icon"));
            }
            request.setLanguage(language);
        }

        // Fechas simplificadas
        request.setProposedDateTime(new Date());
        request.setCreatedAt(new Date());

        return request;
    }
}