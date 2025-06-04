package com.naix.codeswap.models;

import java.util.Date;
import java.util.Map;

public class Conversation {
    private int id;
    private User otherUser;
    private String lastMessage;
    private Date lastMessageTime;
    private int unreadCount;

    public Conversation() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getOtherUser() { return otherUser; }
    public void setOtherUser(User otherUser) { this.otherUser = otherUser; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public Date getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(Date lastMessageTime) { this.lastMessageTime = lastMessageTime; }

    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }

    public static Conversation fromMap(Map<String, Object> data) {
        Conversation conversation = new Conversation();

        if (data.containsKey("id")) {
            conversation.setId(((Double) data.get("id")).intValue());
        }

        if (data.containsKey("other_user")) {
            Map<String, Object> userData = (Map<String, Object>) data.get("other_user");
            User user = new User();
            user.setId(((Double) userData.get("id")).intValue());
            user.setUsername((String) userData.get("username"));

            String firstName = (String) userData.get("first_name");
            String lastName = (String) userData.get("last_name");
            user.setFullName((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));

            conversation.setOtherUser(user);
        }

        if (data.containsKey("last_message")) {
            conversation.setLastMessage((String) data.get("last_message"));
        }

        if (data.containsKey("unread_count")) {
            conversation.setUnreadCount(((Double) data.get("unread_count")).intValue());
        }

        return conversation;
    }
}