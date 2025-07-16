package com.michal.car.notify.service.service;

import com.michal.car.notify.service.bot.TelegramNotifierBot;
import com.michal.car.notify.service.config.GlobalAppProperties;
import com.michal.car.notify.service.model.Car;
import com.michal.car.notify.service.repository.CarRepository;
import com.michal.car.notify.service.scraper.ToyotaWebScraper;
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
    private final CarRepository carRepository;

    private static final String MESSAGE_TEMPLATE = """
            *%s*
            
            [Zobraziť ponuku](%s)
            
            *Cena:* %s
            """;

    private static final Logger LOGGER = LoggerFactory.getLogger(ToyotaWebScraper.class);

    public CarWatcherService(GlobalAppProperties globalAppProperties,
                             TelegramNotifierBot telegramNotifierBot,
                             ToyotaWebScraper toyotaWebScraper,
                             CarRepository carRepository) {
        this.globalAppProperties = globalAppProperties;
        this.telegramNotifierBot = telegramNotifierBot;
        this.toyotaWebScraper = toyotaWebScraper;
        this.carRepository = carRepository;
    }

    public void scrapeDataAndSend() {
        LOGGER.info("Scraping data for [{}] models", globalAppProperties.getWatchCars().size());
        Integer upperPriceLimit = globalAppProperties.getUpperPrice();
        String urlTemplate = globalAppProperties.getBaseUrl();
        String urlSuffixTemplate = globalAppProperties.getSuffixTemplate();

        List<Car> scrapedCars = new ArrayList<>();

        globalAppProperties.getWatchCars().forEach(carModel -> scrapedCars.addAll(toyotaWebScraper.getCars(urlTemplate, urlSuffixTemplate, carModel, upperPriceLimit)));

        List<Car> newRecords = carRepository.syncDataAndReturnOnlyNewEntries(scrapedCars);
        LOGGER.info("Actual new records [{}]", newRecords.size());

        newRecords.forEach(foundCar -> {
            String caption = MESSAGE_TEMPLATE.formatted(
                    foundCar.title(),
                    foundCar.url(),
                    foundCar.price()
            );
            telegramNotifierBot.broadcastPhoto(caption, foundCar.photoUrl());
        });
    }
}
