package com.michal.car.notify.service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michal.car.notify.service.exception.ApplicationException;
import com.michal.car.notify.service.model.FoundCar;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Michal Remis
 */
@Service
public class FoundCarService {

    private final ObjectMapper objectMapper;

    private final Map<String, FoundCar> repository = new ConcurrentHashMap<>();

    private final Path targetFilePath;

    private static final Logger LOGGER = LoggerFactory.getLogger(FoundCarService.class);

    public FoundCarService(ObjectMapper objectMapper,
                           @Value("${in-memory-repo.car.file-path}") String dataSourceFilePath) {
        this.objectMapper = objectMapper;
        targetFilePath = Paths.get(dataSourceFilePath);
    }

    @PostConstruct
    private void init() {
        if (Files.exists(targetFilePath)) {
            LOGGER.info("cars.json found, loading");
            try {
                Map<String, FoundCar> loaded = objectMapper.readValue(Files.newBufferedReader(targetFilePath), new TypeReference<>() {});
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

    public List<FoundCar> syncDataAndReturnOnlyNewEntries(List<FoundCar> incoming) {
        List<FoundCar> newlyAdded = new ArrayList<>();

        Map<String, FoundCar> incomingMap = incoming.stream()
                .collect(Collectors.toMap(FoundCar::id, Function.identity()));

        incoming.forEach(fresh -> {
            FoundCar existing = repository.get(fresh.id());
            if (existing == null || !existing.equals(fresh)) {
                newlyAdded.add(fresh);
            }
        });

        repository.clear();
        repository.putAll(incomingMap);
        overwrite();
        return newlyAdded;
    }

    public List<FoundCar> findAllFoundAfterWithPriceLimit(Set<String> model, Instant foundAfter, Integer upperPrice) {
        return repository.values().stream()
                .filter(car -> car.createdAt().isAfter(foundAfter) && car.price() <= upperPrice && model.contains(car.model()))
                .toList();
    }

    public List<FoundCar> findAll() {
        return repository.values().stream().toList();
    }

    private void overwrite() {
        LOGGER.info("Commiting changes to [{}] file", targetFilePath.getFileName());
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(targetFilePath.toFile(), repository);
            LOGGER.info("[{}] overwritten with [{}] entries.", targetFilePath.getFileName(), repository.size());
        } catch (IOException e) {
            throw ApplicationException.of(String.format("Failed to overwrite [%s]", targetFilePath.getFileName()), e);
        }
    }
}
