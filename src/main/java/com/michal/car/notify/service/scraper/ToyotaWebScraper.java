package com.michal.car.notify.service.scraper;

import com.michal.car.notify.service.model.Car;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Michal Remis
 */
@Service
public class ToyotaWebScraper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToyotaWebScraper.class);

    public List<Car> getCars(String baseUrl, String suffixTemplate, String model, Integer upperPrice) {
        LOGGER.info("Fetching car model: [{}], upper price limit: [{}]", model, upperPrice);
        String path = baseUrl
                .concat(suffixTemplate)
                .replace("{UPPER_PRICE}", upperPrice.toString())
                .replace("{MODEL}", model.toLowerCase());

        List<Car> cars = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(path).get();
            Elements cards = doc.select("div.srp__list div.o-bx");

            for (Element card : cards) {
                String id = card.selectFirst("a[href*=/ponuka/]").attr("href").split("/")[3];
                String title = card.selectFirst(".o-bx__header__name__title").text().trim();
                String subtitle = card.selectFirst(".o-bx__header__name__subtitle") != null
                        ? card.selectFirst(".o-bx__header__name__subtitle").text().trim()
                        : "";
                String fullTitle = title + " " + subtitle;

                String link = baseUrl + card.selectFirst("a[href*=/ponuka/]").attr("href");
                String price = card.selectFirst(".o-bx__price strong") != null
                        ? card.selectFirst(".o-bx__price strong").text().replace("\u00a0", " ").trim()
                        : "?";

                String imageUrl = card.selectFirst(".o-bx__image__picture") != null
                        ? baseUrl + card.selectFirst(".o-bx__image__picture")
                        .attr("style")
                        .replaceAll(".*url\\((.*?)\\).*", "$1")
                        : "";

                cars.add(new Car(id, fullTitle, link, imageUrl, price));
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to scrape Toyota website", e);
        }

        LOGGER.info("Found: [{}] cars for model: [{}] and upper price: [{}]", cars.size(), model, upperPrice);
        return cars;
    }
}
