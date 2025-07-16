package com.michal.car.notify.service.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michal.car.notify.service.exception.ApplicationException;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Michal Remis
 */
@Service
public class CarService {

    private final ObjectMapper objectMapper;

    private final Map<String, Car> repository = new ConcurrentHashMap<>();

    private final Path targetFilePath;

    private static final Logger LOGGER = LoggerFactory.getLogger(CarService.class);

    public CarService(ObjectMapper objectMapper,
                      @Value("${in-memory-repo.car.file-path}") String dataSourceFilePath) {
        this.objectMapper = objectMapper;
        targetFilePath = Paths.get(dataSourceFilePath);
    }

    @PostConstruct
    private void init() {
        if (Files.exists(targetFilePath)) {
            LOGGER.info("cars.json found, loading");
            try {
                Map<String, Car> loaded = objectMapper.readValue(Files.newBufferedReader(targetFilePath), new TypeReference<>() {});
                repository.putAll(loaded);
                LOGGER.info("Loaded [{}] cars from cars.json", repository.size());
            } catch (IOException e) {
                throw ApplicationException.of("Failed to read cars.json", e);
            }
        } else {
            try {
                Files.writeString(targetFilePath, "{}");
                LOGGER.info("cars.json not found. Created new empty file.");
            } catch (IOException e) {
                throw ApplicationException.of("Failed to create cars.json", e);
            }
        }
    }

    public List<Car> syncDataAndReturnOnlyNewEntries(List<Car> incoming) {
        List<Car> newlyAdded = new ArrayList<>();

        Map<String, Car> incomingMap = incoming.stream()
                .collect(Collectors.toMap(Car::id, Function.identity()));

        incoming.forEach(fresh -> {
            Car existing = repository.get(fresh.id());
            if (existing == null || !existing.equals(fresh)) {
                newlyAdded.add(fresh);
            }
        });

        repository.clear();
        repository.putAll(incomingMap);

        overwrite();
        return newlyAdded;
    }

    public List<Car> findAll() {
        return repository.values().stream().toList();
    }

    private void overwrite() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(targetFilePath.toFile(), repository);
            LOGGER.info("cars.json overwritten with [{}] entries.", repository.size());
        } catch (IOException e) {
            throw ApplicationException.of("Failed to overwrite cars.json", e);
        }
    }
}
