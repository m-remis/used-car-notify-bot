package com.michal.car.notify.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.michal.car.notify.service.service.ApprovedCarModelsService;
import com.michal.car.notify.service.service.UserService;
import com.michal.car.notify.service.telegram.TelegramNotifierBot;
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
            @Value("${telegrambots.username}") String botUsername,
            @Value("${telegrambots.token}") String botToken,
            GlobalAppProperties globalAppProperties,
            TelegramBotsApi botsApi,
            JsonMessageSource messageSource,
            UserService userService,
            ApprovedCarModelsService approvedCarModelsService
    ) throws TelegramApiException {
        TelegramNotifierBot bot = new TelegramNotifierBot(
                botUsername,
                botToken,
                userService,
                globalAppProperties,
                approvedCarModelsService,
                messageSource
        );
        botsApi.registerBot(bot);
        return bot;
    }
}
