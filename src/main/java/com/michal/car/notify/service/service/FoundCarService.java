package com.michal.car.notify.service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michal.car.notify.service.model.FoundCar;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
public class FoundCarService extends InMemoryBaseRepository<Map<String, FoundCar>> {

    private final Map<String, FoundCar> repository = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(FoundCarService.class);

    public FoundCarService(ObjectMapper objectMapper,
                           @Value("${in-memory-repo.car.file-path}") String dataSourceFilePath) {
        super(objectMapper, dataSourceFilePath, new TypeReference<>() {}, LOGGER);
    }

    @PostConstruct
    private void postConstruct() {
        super.loadData();
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
        super.overwriteData();
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

    @Override
    protected Map<String, FoundCar> createEmptyInstance() {
        return Map.of();
    }

    @Override
    protected int sizeOf(Map<String, FoundCar> repo) {
        return repo.size();
    }
}
