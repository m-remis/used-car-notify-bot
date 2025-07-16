package com.michal.car.notify.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.michal.car.notify.service.bot.TelegramNotifierBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


/**
 * @author Michal Remis
 */
@Configuration
@EnableScheduling
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new ParameterNamesModule());
    }

    @Bean
    public JsonMessageSource jsonMessageSource() {
        return new JsonMessageSource();
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public TelegramNotifierBot telegramNotifierBot(
            @Value("${telegrambots.bots[0].username}") String botUsername,
            @Value("${telegrambots.bots[0].token}") String botToken,
            GlobalAppProperties globalAppProperties,
            TelegramBotsApi botsApi,
            JsonMessageSource messageSource
    ) throws TelegramApiException {
        TelegramNotifierBot bot = new TelegramNotifierBot(botUsername, botToken, globalAppProperties, messageSource);
        botsApi.registerBot(bot);
        return bot;
    }
}
