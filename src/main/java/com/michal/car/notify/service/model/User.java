package com.michal.car.notify.service.model;

import java.util.Objects;

/**
 * @author Michal Remis
 */
public class User {

    private String chatId;
    private Boolean notificationsEnabled;
    private Boolean isApproved;

    public User(String chatId, Boolean notificationsEnabled, Boolean isApproved) {
        this.chatId = chatId;
        this.isApproved = isApproved;
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(chatId, user.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(chatId);
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId='" + chatId + '\'' +
                ", notificationsEnabled=" + notificationsEnabled +
                ", isApproved=" + isApproved +
                '}';
    }
}