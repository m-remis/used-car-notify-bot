package com.michal.car.notify.service.controller;

import com.michal.car.notify.service.config.GlobalAppProperties;
import com.michal.car.notify.service.dto.AppConfigDto;
import com.michal.car.notify.service.model.Car;
import com.michal.car.notify.service.model.User;
import com.michal.car.notify.service.service.CarService;
import com.michal.car.notify.service.service.UserService;
import com.michal.car.notify.service.service.CarWatcherService;
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
    private static final String NOTIFICATION = "/notification/job";
    private static final String NOTIFICATION_EXISTING = "/notification/existing-records";

    private final CarService carService;
    private final UserService userService;
    private final GlobalAppProperties globalAppProperties;
    private final CarWatcherService carWatcherService;

    public AppController(CarService carService,
                         UserService userService,
                         GlobalAppProperties globalAppProperties,
                         CarWatcherService carWatcherService) {
        this.carService = carService;
        this.userService = userService;
        this.globalAppProperties = globalAppProperties;
        this.carWatcherService = carWatcherService;
    }

    @GetMapping(CARS)
    @Operation(summary = "Returns list of saved cars")
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.findAll());
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
                        globalAppProperties.getWatchCars(),
                        globalAppProperties.getUpperPrice(),
                        globalAppProperties.getDisableJob(),
                        globalAppProperties.getAdminChatId()
                )
        );
    }

    @PatchMapping(CONFIG)
    @Operation(summary = "Updates runtime configuration")
    public ResponseEntity<AppConfigDto> updateAppConfig(@RequestBody AppConfigDto requestDto) {
        Optional.ofNullable(requestDto.disableJob()).ifPresent(globalAppProperties::setDisableJob);
        Optional.ofNullable(requestDto.watchCars()).ifPresent(globalAppProperties::setWatchCars);
        Optional.ofNullable(requestDto.upperPrice()).ifPresent(globalAppProperties::setUpperPrice);
        Optional.ofNullable(requestDto.adminChatId()).ifPresent(globalAppProperties::setAdminChatId);

        return ResponseEntity.ok(
                new AppConfigDto(
                        globalAppProperties.getWatchCars(),
                        globalAppProperties.getUpperPrice(),
                        globalAppProperties.getDisableJob(),
                        globalAppProperties.getAdminChatId()
                )
        );
    }

    @PostMapping(NOTIFICATION)
    @Operation(summary = "Force execute scrapping notification job")
    public ResponseEntity<Void> forceExecuteScrappingNotificationJob() {
        carWatcherService.scrapeDataAndSend();
        return ResponseEntity.ok().build();
    }

    @PostMapping(NOTIFICATION_EXISTING)
    @Operation(summary = "Force send all existing records broadcast")
    public ResponseEntity<Void> forceSendAllRecordsBroadcast() {
        carWatcherService.forceSendExisting();
        return ResponseEntity.ok().build();
    }
}
