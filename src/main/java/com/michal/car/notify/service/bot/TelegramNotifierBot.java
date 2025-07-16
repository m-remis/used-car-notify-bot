package com.michal.car.notify.service.bot;

import com.michal.car.notify.service.config.GlobalAppProperties;
import com.michal.car.notify.service.config.JsonMessageSource;
import com.michal.car.notify.service.model.User;
import com.michal.car.notify.service.repository.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Michal Remis
 */
public class TelegramNotifierBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final UserService userService;
    private final JsonMessageSource messageSource;
    private final GlobalAppProperties globalAppProperties;

    private static final String PARSE_MODE_V2 = "MarkdownV2";
    private static final String PARSE_MODE_V1 = "Markdown";

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramNotifierBot.class);

    public TelegramNotifierBot(String botUsername,
                               String botToken,
                               UserService userService,
                               GlobalAppProperties globalAppProperties,
                               JsonMessageSource messageSource) {
        super(botToken);
        this.botUsername = botUsername;
        this.userService = userService;
        this.messageSource = messageSource;
        this.globalAppProperties = globalAppProperties;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        final String requesterChatId = update.getMessage().getChatId().toString();
        final String text = update.getMessage().getText().toLowerCase().strip();
        final boolean isUserAdmin = globalAppProperties.getAdminChatId().equals(requesterChatId);

        if (isUserAdmin) {
            LOGGER.info("Admin request received for chat [{}] with text: [{}]", requesterChatId, text);
            handleAdminRequest(requesterChatId, text);
            return;
        }

        Optional<User> userOpt = userService.findByChatId(requesterChatId);

        if (userOpt.isEmpty()) {
            LOGGER.warn("Unknown user [{}] tried to access. Adding user as unapproved.", requesterChatId);
            userService.addNotApprovedUser(requesterChatId);
            sendText(requesterChatId, messageSource.get("user-added-unapproved"));
            return;
        }

        User user = userOpt.get();
        if (!user.getApproved()) {
            LOGGER.warn("Unapproved user [{}] tried to use the bot", requesterChatId);
            sendText(requesterChatId, messageSource.get("user-still-unapproved"));
            return;
        }

        LOGGER.info("User request received for chat [{}] with text: [{}]", requesterChatId, text);
        handleUserRequest(requesterChatId, text);
    }

    private void handleUserRequest(String chatId, String prompt) {

        switch (prompt) {
            case "/start" -> sendText(chatId, messageSource.get("start-initial-welcome"));

            case "/zapnut" -> {
                userService.setEnabledNotifications(chatId, true);
                sendText(chatId, messageSource.get("start"));
            }

            case "/vypnut" -> {
                userService.setEnabledNotifications(chatId, false);
                sendText(chatId, messageSource.get("stop"));
            }

            case "/manual" -> sendText(chatId, messageSource.get("manual"));

            default -> sendText(chatId, messageSource.get("unknown"));
        }
    }

    private void handleAdminRequest(String chatId, String prompt) {

        if (prompt.startsWith("/approve ")) {
            String targetChatId = prompt.substring("/approve ".length()).trim();

            if (!targetChatId.matches("\\d+")) {
                sendText(chatId, messageSource.get("user-approved-invalid"));
                return;
            }

            if (userService.findByChatId(targetChatId).isPresent()) {
                userService.setApprovedForUser(targetChatId, true);
                String msg = messageSource.get("user-approved-success").replace("{chatId}", targetChatId);
                sendText(chatId, msg);
            } else {
                String msg = messageSource.get("user-approved-fail").replace("{chatId}", targetChatId);
                sendText(chatId, msg);
            }
            return;
        }

        switch (prompt) {
            case "/start" -> sendText(chatId, messageSource.get("start-initial-welcome"));

            case "/zapnut" -> {
                userService.setEnabledNotifications(chatId, true);
                sendText(chatId, messageSource.get("start"));
            }

            case "/vypnut" -> {
                userService.setEnabledNotifications(chatId, false);
                sendText(chatId, messageSource.get("stop"));
            }

            case "/stop-app" -> {
                globalAppProperties.setDisableJob(true);
                sendText(chatId, messageSource.get("stop-app"));
            }

            case "/manual" -> sendText(chatId, messageSource.get("manual-admin"));

            case "/pending-users" -> {
                List<User> pending = userService.findNotApprovedUsers();
                if (pending.isEmpty()) {
                    sendText(chatId, messageSource.get("user-list-pending-empty"));
                } else {
                    String list = pending.stream()
                            .map(User::getChatId)
                            .collect(Collectors.joining("\n"));
                    String msg = messageSource.get("user-list-pending").replace("{list}", list);
                    sendText(chatId, msg);
                }
            }

            case "/approved-users" -> {
                List<User> approved = userService.findAllApprovedUsers();
                if (approved.isEmpty()) {
                    sendText(chatId, messageSource.get("user-list-approved-empty"));
                } else {
                    String list = approved.stream()
                            .map(User::getChatId)
                            .collect(Collectors.joining("\n"));
                    String msg = messageSource.get("user-list-approved").replace("{list}", list);
                    sendText(chatId, msg);
                }
            }

            default -> sendText(chatId, messageSource.get("unknown"));
        }
    }

    public void broadcastText(String caption) {
        userService.findAllApprovedWithEnabledNotifications().forEach(user -> sendText(user.getChatId(), caption));
    }

    public void sendText(String chatId, String message) {
        SendMessage msg = new SendMessage(chatId, message);
        msg.setParseMode(PARSE_MODE_V2);
        try {
            execute(msg);
        } catch (Exception e) {
            LOGGER.error("Failed to send message", e);
        }
    }

    public void broadcastPhoto(String caption, String imageUrl) {
        userService.findAllApprovedWithEnabledNotifications().forEach(user -> sendPhoto(user.getChatId(), caption, imageUrl));
    }

    public void sendPhoto(String chatId, String caption, String imageUrl) {
        LOGGER.info("Sending telegram photo message....");
        SendPhoto msg = new SendPhoto();
        msg.setChatId(chatId);
        msg.setCaption(caption);
        msg.setPhoto(new InputFile(imageUrl));
        msg.setParseMode(PARSE_MODE_V1);
        try {
            execute(msg);
        } catch (Exception e) {
            LOGGER.error("Failed to send photo", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onRegister() {
        LOGGER.info("Telegram bot [{}] registered with TelegramBots API", getBotUsername());
        super.onRegister();
    }
}