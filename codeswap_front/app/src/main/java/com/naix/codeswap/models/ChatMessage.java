package com.naix.codeswap.models;

import java.util.Date;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChatMessage {
    private int id;
    private User sender;
    private String content;
    private Date createdAt;
    private boolean isOwn;

    public ChatMessage() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public boolean isOwn() { return isOwn; }
    public void setOwn(boolean own) { isOwn = own; }

    public static ChatMessage fromMap(Map<String, Object> data) {
        ChatMessage message = new ChatMessage();

        if (data.containsKey("id")) {
            message.setId(((Double) data.get("id")).intValue());
        }

        if (data.containsKey("content")) {
            message.setContent((String) data.get("content"));
        }

        if (data.containsKey("is_own")) {
            message.setOwn((Boolean) data.get("is_own"));
        }

        if (data.containsKey("sender")) {
            Map<String, Object> senderData = (Map<String, Object>) data.get("sender");
            User sender = new User();
            sender.setId(((Double) senderData.get("id")).intValue());
            sender.setUsername((String) senderData.get("username"));
            message.setSender(sender);
        }

        // Parsear fecha simplificado
        message.setCreatedAt(new Date());

        return message;
    }
}