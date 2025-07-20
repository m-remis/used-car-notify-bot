package com.michal.car.notify.service.scheduled;

import com.michal.car.notify.service.config.GlobalAppProperties;
import com.michal.car.notify.service.service.Coordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

/**
 * @author Michal Remis
 */
@Service
public class JobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);

    private final GlobalAppProperties globalAppProperties;
    private final Coordinator coordinator;

    public JobScheduler(GlobalAppProperties globalAppProperties,
                        Coordinator coordinator) {
        this.globalAppProperties = globalAppProperties;
        this.coordinator = coordinator;
    }

    @Scheduled(
            fixedDelayString = "${global-app-props.jobs.car-scraping.fixed-delay}",
            initialDelayString = "${global-app-props.jobs.car-scraping.initial-delay}"
    )
    public void carScrapperJob() {
        if (!globalAppProperties.getDisableJob()) {

            StopWatch stopWatch = new StopWatch();
            stopWatch.start("Car scraping job");

            LOGGER.info("Executing Car scraping job...");
            coordinator.scrapeCarData();

            stopWatch.stop();
            LOGGER.info("Car scraping job finished. Elapsed time: [{}] seconds", stopWatch.getTotalTimeSeconds());

        } else {
            LOGGER.info("Jobs are disabled");
        }
    }

    @Scheduled(
            fixedDelayString = "${global-app-props.jobs.user-notification.fixed-delay}",
            initialDelayString = "${global-app-props.jobs.user-notification.initial-delay}"
    )
    public void userNotificationJob() {
        if (!globalAppProperties.getDisableJob()) {

            StopWatch stopWatch = new StopWatch();
            stopWatch.start("User notification job");

            LOGGER.info("Executing user notification job...");
            coordinator.sendNotificationsToUsers();

            stopWatch.stop();
            LOGGER.info("User notification job finished. Elapsed time: [{}] seconds", stopWatch.getTotalTimeSeconds());

        } else {
            LOGGER.info("Jobs are disabled");
        }
    }
}
