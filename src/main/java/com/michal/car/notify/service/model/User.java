package com.michal.car.notify.service.model;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * @author Michal Remis
 */
public class User {

    private String chatId;
    private Boolean notificationsEnabled;
    private Boolean isApproved;
    private Set<String> watchForModels;
    private Integer upperPriceLimit;
    private Instant lastNotificationReceivedAt;

    public User(String chatId,
                Boolean notificationsEnabled,
                Boolean isApproved,
                Integer upperPriceLimit,
                Set<String> watchForModels,
                Instant lastNotificationReceivedAt) {
        this.chatId = chatId;
        this.isApproved = isApproved;
        this.notificationsEnabled = notificationsEnabled;
        this.watchForModels = watchForModels;
        this.upperPriceLimit = upperPriceLimit;
        this.lastNotificationReceivedAt = lastNotificationReceivedAt;
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

    public Set<String> getWatchForModels() {
        return watchForModels;
    }

    public void setWatchForModels(Set<String> watchForModels) {
        this.watchForModels = watchForModels;
    }

    public Integer getUpperPriceLimit() {
        return upperPriceLimit;
    }

    public void setUpperPriceLimit(Integer upperPriceLimit) {
        this.upperPriceLimit = upperPriceLimit;
    }

    public Instant getLastNotificationReceivedAt() {
        return lastNotificationReceivedAt;
    }

    public void setLastNotificationReceivedAt(Instant lastNotificationReceivedAt) {
        this.lastNotificationReceivedAt = lastNotificationReceivedAt;
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
                ", watchForModels=" + watchForModels +
                ", upperPriceLimit=" + upperPriceLimit +
                ", lastNotificationReceivedAt=" + lastNotificationReceivedAt +
                '}';
    }
}