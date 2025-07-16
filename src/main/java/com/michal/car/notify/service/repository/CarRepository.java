package com.michal.car.notify.service.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michal.car.notify.service.model.Car;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Michal Remis
 */
@Service
public class CarRepository {

    private final ObjectMapper objectMapper;

    private final Map<String, Car> cars = new HashMap<>();

    private static Path targetFilePath;

    private static final Logger LOGGER = LoggerFactory.getLogger(CarRepository.class);

    public CarRepository(ObjectMapper objectMapper,
                         @Value("${car-repo.file-path}") String dataSourceFilePath) {
        this.objectMapper = objectMapper;
        targetFilePath = Paths.get(dataSourceFilePath);
    }

    @PostConstruct
    private void init() {
        if (Files.exists(targetFilePath)) {
            LOGGER.info("cars.json found, loading");
            try {
                Map<String, Car> loaded = objectMapper.readValue(Files.newBufferedReader(targetFilePath), new TypeReference<>() {});
                cars.putAll(loaded);
                LOGGER.info("Loaded [{}] cars from cars.json", cars.size());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read cars.json", e);
            }
        } else {
            try {
                Files.writeString(targetFilePath, "{}");
                LOGGER.info("cars.json not found. Created new empty file.");
            } catch (IOException e) {
                throw new RuntimeException("Failed to create cars.json", e);
            }
        }
    }

    public List<Car> syncDataAndReturnOnlyNewEntries(List<Car> incoming) {
        List<Car> newlyAdded = new ArrayList<>();

        Map<String, Car> incomingMap = incoming.stream()
                .collect(Collectors.toMap(Car::id, Function.identity()));

        incoming.forEach(fresh -> {
            Car existing = cars.get(fresh.id());
            if (existing == null || !existing.equals(fresh)) {
                newlyAdded.add(fresh);
            }
        });

        cars.clear();
        cars.putAll(incomingMap);

        overwrite();
        return newlyAdded;
    }

    public List<Car> findAll() {
        return cars.values().stream().toList();
    }

    private void overwrite() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(targetFilePath.toFile(), cars);
            LOGGER.info("cars.json overwritten with [{}] entries.", cars.size());
        } catch (IOException e) {
            throw new RuntimeException("Failed to overwrite cars.json", e);
        }
    }
}
