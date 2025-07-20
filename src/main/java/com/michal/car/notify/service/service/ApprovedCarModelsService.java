package com.michal.car.notify.service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michal.car.notify.service.exception.ApplicationException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApprovedCarModelsService {

    private final ObjectMapper objectMapper;

    private final Set<String> repository = new HashSet<>();

    private final Path targetFilePath;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApprovedCarModelsService.class);

    public ApprovedCarModelsService(ObjectMapper objectMapper,
                                    @Value("${in-memory-repo.known-car-models.file-path}") String dataSourceFilePath) {
        this.objectMapper = objectMapper;
        targetFilePath = Paths.get(dataSourceFilePath);
    }

    @PostConstruct
    private void init() {
        if (Files.exists(targetFilePath)) {
            LOGGER.info("[{}] found, loading", targetFilePath.getFileName());
            try {
                Set<String> loaded = objectMapper.readValue(Files.newBufferedReader(targetFilePath), new TypeReference<>() {
                });
                // load them all in lowercase
                repository.addAll(loaded.stream().map(String::toLowerCase).collect(Collectors.toSet()));
                LOGGER.info("Loaded [{}] car models from [{}]", repository.size(), targetFilePath.getFileName());
            } catch (IOException e) {
                throw ApplicationException.of(String.format("Failed to read [%s]", targetFilePath.getFileName()), e);
            }
        } else {
            try {
                Files.writeString(targetFilePath, "[]");
                LOGGER.info("[{}] not found. Created new empty file.", targetFilePath.getFileName());
            } catch (IOException e) {
                throw ApplicationException.of(String.format("Failed to create [%s]", targetFilePath.getFileName()), e);
            }
        }
    }

    public Set<String> findAll() {
        return repository;
    }

    public Set<String> overWriteAllApprovedCarModels(Set<String> newCarModels) {
        repository.clear();
        repository.addAll(newCarModels);
        overwrite();
        return findAll();
    }

    public void addNew(String newModel) {
        repository.add(newModel.toLowerCase());
        overwrite();
    }

    public boolean isApprovedModel(String model) {
        return repository.contains(model.toLowerCase());
    }

    public boolean areApprovedModels(List<String> models) {
        return repository.containsAll(models.stream().map(String::toLowerCase).collect(Collectors.toSet()));
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
