package com.michal.car.notify.service.service;

import com.michal.car.notify.service.config.GlobalAppProperties;
import com.michal.car.notify.service.model.FoundCar;
import com.michal.car.notify.service.model.User;
import com.michal.car.notify.service.scraper.ToyotaWebScraper;
import com.michal.car.notify.service.telegram.TelegramNotifierBot;
import com.michal.car.notify.service.util.MessageFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Michal Remis
 */
@Service
public class Coordinator {

    private final GlobalAppProperties globalAppProperties;
    private final TelegramNotifierBot telegramNotifierBot;
    private final ToyotaWebScraper toyotaWebScraper;
    private final FoundCarService foundCarService;

    private static final Logger LOGGER = LoggerFactory.getLogger(Coordinator.class);
    private final UserService userService;

    public Coordinator(GlobalAppProperties globalAppProperties,
                       TelegramNotifierBot telegramNotifierBot,
                       ToyotaWebScraper toyotaWebScraper,
                       FoundCarService foundCarService, UserService userService) {
        this.globalAppProperties = globalAppProperties;
        this.telegramNotifierBot = telegramNotifierBot;
        this.toyotaWebScraper = toyotaWebScraper;
        this.foundCarService = foundCarService;
        this.userService = userService;
    }

    public void scrapeCarData() {
        String urlTemplate = globalAppProperties.getBaseUrl();
        String urlSuffixTemplate = globalAppProperties.getSuffixTemplate();
        // aggregate max price and all distinct models across users
        Integer upperPriceLimit = userService.getMaxPrice();
        Set<String> searchCarModels = userService.getAllDistinctWatchedCarModels();

        LOGGER.info("Scraping data for [{}] models and upper price: [{}]", searchCarModels.size(), upperPriceLimit);

        List<FoundCar> scrapedCars = new ArrayList<>();

        searchCarModels.forEach(carModel -> scrapedCars.addAll(toyotaWebScraper.getCars(urlTemplate, urlSuffixTemplate, carModel, upperPriceLimit)));

        List<FoundCar> newRecords = foundCarService.syncDataAndReturnOnlyNewEntries(scrapedCars);
        LOGGER.info("Actual new records [{}]", newRecords.size());
    }

    // todo: make this shit concurrent when sending notifications -> thread per user
    public void sendNotificationsToUsers() {
        List<User> users = userService.findAllApprovedWithEnabledNotifications();

        if (users.isEmpty()) {
            LOGGER.warn("No approved users with notifications enabled — skipping send phase.");
            return;
        }

        LOGGER.info("Sending notifications to [{}] users", users.size());

        users.forEach(user -> {
            List<FoundCar> carsToSend = foundCarService.findAllFoundAfterWithPriceLimit(user.getWatchForModels(), user.getLastNotificationReceivedAt(), user.getUpperPriceLimit());

            if (carsToSend.isEmpty()) {
                LOGGER.debug("[{}] - No new cars to notify", user.getChatId());
                return;
            }

            LOGGER.info("CharId: [{}] - Sending [{}] cars", user.getChatId(), carsToSend.size());

            carsToSend.forEach(car -> {
                telegramNotifierBot.sendPhoto(user.getChatId(), MessageFormatter.formatCarMessage(car), car.photoUrl());
            });
        });

        LOGGER.info("Notification dispatch complete");
    }

    public void forceSendExisting() {
        LOGGER.info("Force send existing");
        foundCarService.findAll().forEach(foundCar -> telegramNotifierBot.broadcastPhoto(MessageFormatter.formatCarMessage(foundCar), foundCar.photoUrl()));
    }
}
