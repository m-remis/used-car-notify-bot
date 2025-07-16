package com.michal.car.notify.service.bot;

import com.michal.car.notify.service.config.GlobalAppProperties;
import com.michal.car.notify.service.config.JsonMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;


public class TelegramNotifierBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final JsonMessageSource messageSource;
    private final GlobalAppProperties globalAppProperties;

    private static final String PARSE_MODE_V2 = "MarkdownV2";
    private static final String PARSE_MODE_V1 = "Markdown";

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramNotifierBot.class);

    public TelegramNotifierBot(String botUsername,
                               String botToken,
                               GlobalAppProperties globalAppProperties,
                               JsonMessageSource messageSource) {
        super(botToken);
        this.botUsername = botUsername;
        this.messageSource = messageSource;
        this.globalAppProperties = globalAppProperties;
    }

    @Override
    public void onUpdateReceived(Update update) {
        final String requesterChatId = update.getMessage().getChatId().toString();

        if (!globalAppProperties.getClients().contains(requesterChatId)) return;
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        final String text = update.getMessage().getText().toLowerCase().strip();

        switch (text) {
            case "/start" -> sendText(requesterChatId, messageSource.get("start-initial-welcome"));
            case "start" -> {
                globalAppProperties.setDisableScraping(false);
                sendText(requesterChatId, messageSource.get("start"));
            }
            case "stop" -> {
                globalAppProperties.setDisableScraping(true);
                sendText(requesterChatId, messageSource.get("stop"));
            }
            case "manual" -> sendText(requesterChatId, messageSource.get("manual"));
            default -> sendText(requesterChatId, messageSource.get("unknown"));
        }
    }

    public void broadcastText(String caption) {
        globalAppProperties.getClients().forEach(chatId -> sendText(chatId, caption));
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
        globalAppProperties.getClients().forEach(chatId -> sendPhoto(chatId, caption, imageUrl));
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