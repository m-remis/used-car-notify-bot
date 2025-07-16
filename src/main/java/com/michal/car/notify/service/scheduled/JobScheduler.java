package com.michal.car.notify.service.scheduled;

import com.michal.car.notify.service.config.GlobalAppProperties;
import com.michal.car.notify.service.service.CarWatcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author Michal Remis
 */
@Service
public class JobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);

    private final GlobalAppProperties globalAppProperties;
    private final CarWatcherService carWatcherService;

    public JobScheduler(GlobalAppProperties globalAppProperties,
                        CarWatcherService carWatcherService) {
        this.globalAppProperties = globalAppProperties;
        this.carWatcherService = carWatcherService;
    }

    @Scheduled(
            fixedDelayString = "${global-app-props.jobs.fixed-delay}",
            initialDelayString = "${global-app-props.jobs.initial-delay}"
    )
    public void carNotificationJob() {
        if (!globalAppProperties.getDisableScraping()) {
            LOGGER.info("Executing Car scraping job");
            carWatcherService.scrapeDataAndSend();
        } else {
            LOGGER.info("Car scraping job is disabled");
        }
    }
}
