package com.michal.car.notify.service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michal.car.notify.service.exception.ApplicationException;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InMemoryBaseRepository<T> {

    protected final ObjectMapper objectMapper;
    protected final Path targetFilePath;
    protected final Logger logger;

    protected T repository;
    private final TypeReference<T> typeReference;

    protected InMemoryBaseRepository(ObjectMapper objectMapper,
                                     String filePath,
                                     TypeReference<T> typeReference,
                                     Logger logger) {
        this.objectMapper = objectMapper;
        this.targetFilePath = Paths.get(filePath);
        this.typeReference = typeReference;
        this.logger = logger;
        this.repository = createEmptyInstance();
    }

    protected void loadData() {
        if (Files.exists(targetFilePath)) {
            logger.info("[{}] found, loading", targetFilePath.getFileName());
            try (BufferedReader reader = Files.newBufferedReader(targetFilePath)) {
                repository = objectMapper.readValue(reader, typeReference);
                logger.info("Loaded [{}] entries from [{}]", sizeOf(repository), targetFilePath.getFileName());
            } catch (IOException e) {
                throw ApplicationException.of("Failed to read [%s]".formatted(targetFilePath.getFileName()), e);
            }
        } else {
            try {
                Files.writeString(targetFilePath, defaultSerializedEmpty());
                logger.info("[{}] not found. Created new empty file.", targetFilePath.getFileName());
            } catch (IOException e) {
                throw ApplicationException.of("Failed to create [%s]".formatted(targetFilePath.getFileName()), e);
            }
        }
    }

    protected void overwriteData() {
        logger.info("Committing changes to [{}]", targetFilePath.getFileName());
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(targetFilePath.toFile(), repository);
            logger.info("[{}] overwritten with [{}] entries.", targetFilePath.getFileName(), sizeOf(repository));
        } catch (IOException e) {
            throw ApplicationException.of("Failed to overwrite [%s]".formatted(targetFilePath.getFileName()), e);
        }
    }

    protected int sizeOf(T repo) {
        return 0;
    }

    protected String defaultSerializedEmpty() {
        return "{}";
    }

    protected T createEmptyInstance() {
        return null;
    }
}
