package com.michal.car.notify.service.controller;

import com.michal.car.notify.service.config.GlobalAppProperties;
import com.michal.car.notify.service.model.Car;
import com.michal.car.notify.service.repository.CarRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/management")
public class AppController {

    private static final String CARS = "/cars";
    private static final String CONFIG = "/config";

    private final CarRepository carRepository;
    private final GlobalAppProperties globalAppProperties;

    public AppController(CarRepository carRepository,
                         GlobalAppProperties globalAppProperties) {
        this.carRepository = carRepository;
        this.globalAppProperties = globalAppProperties;
    }

    @GetMapping(CARS)
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carRepository.findAll());
    }

    @GetMapping(CONFIG)
    public ResponseEntity<String> getProps() {
        return ResponseEntity.ok(globalAppProperties.toString());
    }

}
