package com.michal.car.notify.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ApprovedCarModelsServiceTest {

    @TempDir
    static Path tempDir;

    private static ApprovedCarModelsService service;

    @BeforeAll
    static void setup() {
        Path jsonFile = tempDir.resolve("approved-models.json");
        service = new ApprovedCarModelsService(new ObjectMapper(), jsonFile.toString());
    }

    @Test
    void shouldAddAndVerifyApprovedModel() {
        service.addNew("Corolla");

        assertThat(service.findAll()).containsExactly("corolla");
        assertThat(service.isApprovedModel("COROLLA")).isTrue();
        assertThat(service.isApprovedModel("rav4")).isFalse();
    }

    @Test
    void shouldOverwriteModelsCorrectly() {
        Set<String> newSet = Set.of("auris", "yaris");
        service.overWriteAllApprovedCarModels(newSet);

        assertThat(service.findAll()).containsExactlyInAnyOrder("auris", "yaris");
        assertThat(service.isApprovedModel("yaris")).isTrue();
    }

    @Test
    void shouldValidateMultipleModels() {
        service.overWriteAllApprovedCarModels(Set.of("corolla", "rav4", "yaris"));

        boolean valid = service.areApprovedModels(List.of("corolla", "rav4"));
        boolean invalid = service.areApprovedModels(List.of("corolla", "civic"));

        assertThat(valid).isTrue();
        assertThat(invalid).isFalse();
    }
}
