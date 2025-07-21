package com.michal.car.notify.service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApprovedCarModelsService extends InMemoryBaseRepository<Set<String>> {

    private final Set<String> repository = new HashSet<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ApprovedCarModelsService.class);

    public ApprovedCarModelsService(ObjectMapper objectMapper,
                                    @Value("${in-memory-repo.known-car-models.file-path}") String dataSourceFilePath) {
        super(objectMapper, dataSourceFilePath, new TypeReference<>() {}, LOGGER);
    }

    @PostConstruct
    private void postConstruct() {
        super.loadData();
    }

    public Set<String> findAll() {
        return repository;
    }

    public Set<String> overWriteAllApprovedCarModels(Set<String> newCarModels) {
        repository.clear();
        repository.addAll(newCarModels);
        super.overwriteData();
        return findAll();
    }

    public void addNew(String newModel) {
        repository.add(newModel.toLowerCase());
        super.overwriteData();
    }

    public boolean isApprovedModel(String model) {
        return repository.contains(model.toLowerCase());
    }

    public boolean areApprovedModels(List<String> models) {
        return repository.containsAll(models.stream().map(String::toLowerCase).collect(Collectors.toSet()));
    }

    @Override
    protected Set<String> createEmptyInstance() {
        return Set.of();
    }

    @Override
    protected String defaultSerializedEmpty() {
        return "[]";
    }

    @Override
    protected int sizeOf(Set<String> repo) {
        return repo.size();
    }
}
