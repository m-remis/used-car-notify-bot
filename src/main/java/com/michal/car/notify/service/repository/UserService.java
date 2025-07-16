package com.michal.car.notify.service.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michal.car.notify.service.exception.ApplicationException;
import com.michal.car.notify.service.model.User;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michal Remis
 */
@Service
public class UserService {

    private final ObjectMapper objectMapper;

    private final Map<String, User> repository = new ConcurrentHashMap<>();

    private final Path targetFilePath;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public UserService(ObjectMapper objectMapper,
                       @Value("${in-memory-repo.users.file-path}") String dataSourceFilePath) {
        this.objectMapper = objectMapper;
        targetFilePath = Paths.get(dataSourceFilePath);
    }

    @PostConstruct
    private void init() {
        if (Files.exists(targetFilePath)) {
            LOGGER.info("users.json found, loading");
            try {
                Map<String, User> loaded = objectMapper.readValue(Files.newBufferedReader(targetFilePath), new TypeReference<>() {
                });
                repository.putAll(loaded);
                LOGGER.info("Loaded [{}] users from users.json", repository.size());
            } catch (IOException e) {
                throw ApplicationException.of("Failed to read users.json", e);
            }
        } else {
            try {
                Files.writeString(targetFilePath, "{}");
                LOGGER.info("users.json not found. Created new empty file.");
            } catch (IOException e) {
                throw ApplicationException.of("Failed to create users.json", e);
            }
        }
    }

    public List<User> findAll() {
        return repository.values().stream().toList();
    }

    public List<User> overwriteAllUsers(List<User> users) {
        repository.clear();
        users.forEach(user -> repository.putIfAbsent(user.getChatId(), user));
        overwrite();
        return findAll();
    }

    public Optional<User> findByChatId(String chatId) {
        return Optional.ofNullable(repository.get(chatId));
    }

    public void setEnabledNotifications(String chatId, Boolean enabled) {
        findByChatId(chatId).ifPresent(found -> {
            LOGGER.info("Notifications for chatId [{}] set to [{}]", chatId, enabled);
            found.setNotificationsEnabled(enabled);
        });
        overwrite();
    }

    public void addNotApprovedUser(String chatId) {
        if (repository.containsKey(chatId)) {
            LOGGER.info("ChatId [{}] already present", chatId);
        } else {
            LOGGER.info("Adding a new chatId [{}]", chatId);
            repository.putIfAbsent(chatId, new User(chatId, true, false));
            overwrite();
        }
    }

    public List<User> findNotApprovedUsers() {
        return repository.values().stream().filter(candidate -> !candidate.getApproved()).toList();
    }

    public void setApprovedForUser(String chatId, Boolean approved) {
        findByChatId(chatId).ifPresentOrElse(found -> {
            LOGGER.info("Setting approved for chatId [{}] to [{}]", chatId, approved);
            found.setApproved(approved);
            overwrite();
        }, () -> LOGGER.info("ChatId [{}] not found", chatId));
    }

    public List<User> findAllApprovedWithEnabledNotifications() {
        return findAll().stream().filter(candidate -> candidate.getNotificationsEnabled() && candidate.getApproved()).toList();
    }

    public List<User> findAllApprovedUsers() {
        return findAll().stream().filter(User::getApproved).toList();
    }

    private void overwrite() {
        LOGGER.info("Commiting changes to users.json file");
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(targetFilePath.toFile(), repository);
            LOGGER.info("users.json overwritten with [{}] entries.", repository.size());
        } catch (IOException e) {
            throw ApplicationException.of("Failed to overwrite users.json", e);
        }
    }

}
