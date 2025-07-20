package com.michal.car.notify.service.controller;

import com.michal.car.notify.service.config.GlobalAppProperties;
import com.michal.car.notify.service.dto.AppConfigDto;
import com.michal.car.notify.service.model.FoundCar;
import com.michal.car.notify.service.model.User;
import com.michal.car.notify.service.service.FoundCarService;
import com.michal.car.notify.service.service.UserService;
import com.michal.car.notify.service.service.Coordinator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author Michal Remis
 */
@RestController
@RequestMapping("/management")
public class AppController {

    private static final String CARS = "/cars";
    private static final String CONFIG = "/config";
    private static final String USERS = "/users";
    private static final String NOTIFICATION = "/notification";
    private static final String JOB = "/job";

    private final FoundCarService foundCarService;
    private final UserService userService;
    private final GlobalAppProperties globalAppProperties;
    private final Coordinator coordinator;

    public AppController(FoundCarService foundCarService,
                         UserService userService,
                         GlobalAppProperties globalAppProperties,
                         Coordinator coordinator) {
        this.foundCarService = foundCarService;
        this.userService = userService;
        this.globalAppProperties = globalAppProperties;
        this.coordinator = coordinator;
    }

    @GetMapping(CARS)
    @Operation(summary = "Returns list of saved cars")
    public ResponseEntity<List<FoundCar>> getAllCars() {
        return ResponseEntity.ok(foundCarService.findAll());
    }

    @GetMapping(USERS)
    @Operation(summary = "Returns list of users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PostMapping(USERS)
    @Operation(summary = "Updates list of users")
    public ResponseEntity<List<User>> updateAllUsers(@RequestBody List<User> users) {
        return ResponseEntity.ok(userService.overwriteAllUsers(users));
    }

    @GetMapping(CONFIG)
    @Operation(summary = "Returns configurable runtime configuration")
    public ResponseEntity<AppConfigDto> getAppConfig() {
        return ResponseEntity.ok(
                new AppConfigDto(
                        globalAppProperties.getDisableJob(),
                        globalAppProperties.getAdminChatId()
                )
        );
    }

    @PatchMapping(CONFIG)
    @Operation(summary = "Updates runtime configuration")
    public ResponseEntity<AppConfigDto> updateAppConfig(@RequestBody AppConfigDto requestDto) {
        Optional.ofNullable(requestDto.disableJob()).ifPresent(globalAppProperties::setDisableJob);
        Optional.ofNullable(requestDto.adminChatId()).ifPresent(globalAppProperties::setAdminChatId);

        return ResponseEntity.ok(
                new AppConfigDto(
                        globalAppProperties.getDisableJob(),
                        globalAppProperties.getAdminChatId()
                )
        );
    }

    @PostMapping(NOTIFICATION)
    @Operation(summary = "Force send notifications to users")
    public ResponseEntity<Void> forceSendNotifications() {
        coordinator.sendNotificationsToUsers();
        return ResponseEntity.ok().build();
    }

    @PostMapping(JOB)
    @Operation(summary = "Force execute car scrapping job")
    public ResponseEntity<Void> forceExecuteScrappingJob() {
        coordinator.scrapeCarData();
        return ResponseEntity.ok().build();
    }
}
