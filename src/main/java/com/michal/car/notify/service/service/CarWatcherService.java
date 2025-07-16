package com.michal.car.notify.service.service;

import com.michal.car.notify.service.bot.TelegramNotifierBot;
import com.michal.car.notify.service.config.GlobalAppProperties;
import com.michal.car.notify.service.model.Car;
import com.michal.car.notify.service.scraper.ToyotaWebScraper;
import com.michal.car.notify.service.util.MessageFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michal Remis
 */
@Service
public class CarWatcherService {

    private final GlobalAppProperties globalAppProperties;
    private final TelegramNotifierBot telegramNotifierBot;
    private final ToyotaWebScraper toyotaWebScraper;
    private final CarService carService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CarWatcherService.class);

    public CarWatcherService(GlobalAppProperties globalAppProperties,
                             TelegramNotifierBot telegramNotifierBot,
                             ToyotaWebScraper toyotaWebScraper,
                             CarService carService) {
        this.globalAppProperties = globalAppProperties;
        this.telegramNotifierBot = telegramNotifierBot;
        this.toyotaWebScraper = toyotaWebScraper;
        this.carService = carService;
    }

    public void scrapeDataAndSend() {
        LOGGER.info("Scraping data for [{}] models", globalAppProperties.getWatchCars().size());
        Integer upperPriceLimit = globalAppProperties.getUpperPrice();
        String urlTemplate = globalAppProperties.getBaseUrl();
        String urlSuffixTemplate = globalAppProperties.getSuffixTemplate();

        List<Car> scrapedCars = new ArrayList<>();

        globalAppProperties.getWatchCars().forEach(carModel -> scrapedCars.addAll(toyotaWebScraper.getCars(urlTemplate, urlSuffixTemplate, carModel, upperPriceLimit)));

        List<Car> newRecords = carService.syncDataAndReturnOnlyNewEntries(scrapedCars);
        LOGGER.info("Actual new records [{}]", newRecords.size());

        newRecords.forEach(foundCar -> telegramNotifierBot.broadcastPhoto(MessageFormatter.formatCarMessage(foundCar), foundCar.photoUrl()));
    }

    public void forceSendExisting() {
        LOGGER.info("Force send existing");
        carService.findAll().forEach(foundCar -> telegramNotifierBot.broadcastPhoto(MessageFormatter.formatCarMessage(foundCar), foundCar.photoUrl()));
    }
}
