package com.michal.car.notify.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michal.car.notify.service.model.FoundCar;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FoundCarServiceTest {

    @TempDir
    static Path tempDir;

    private static FoundCarService service;

    @BeforeAll
    static void setup() {
        Path jsonFile = tempDir.resolve("cars.json");
        service = new FoundCarService(new ObjectMapper(), jsonFile.toString());
    }

    @AfterEach
    void cleanUp() {
        service.createEmptyInstance();
    }

    @Test
    void shouldSyncNewCarsOnly() {
        FoundCar carA = car("a1", "corollaTitle", "corolla", 10000, Instant.now());
        FoundCar carB = car("b1", "rav4Title", "rav4", 12000, Instant.now());

        List<FoundCar> newOnes = service.syncDataAndReturnOnlyNewEntries(List.of(carA, carB));
        assertThat(newOnes).hasSize(2);

        List<FoundCar> secondRun = service.syncDataAndReturnOnlyNewEntries(List.of(carA, carB));
        assertThat(secondRun).isEmpty();

        FoundCar carAUpdated = car("a1", "corollaTitle", "corolla", 9500, Instant.now());
        List<FoundCar> diff = service.syncDataAndReturnOnlyNewEntries(List.of(carAUpdated, carB));
        assertThat(diff).containsExactly(carAUpdated);
    }

    @Test
    void shouldFilterByTimeModelAndPrice() {
        Instant now = Instant.now();
        Instant past = now.minusSeconds(3600);

        service.syncDataAndReturnOnlyNewEntries(List.of(
                car("x", "corollaTitle", "corolla", 9999, now),
                car("y", "rav4Title", "rav4", 14500, now),
                car("z", "supraTitle", "supra", 30000, past)  // filtered out by time
        ));

        List<FoundCar> result = service.findAllFoundAfterWithPriceLimit(
                Set.of("corolla", "rav4", "supra"), past.plusSeconds(1), 30000
        );

        assertThat(result).hasSize(2);
        assertThat(result).extracting(FoundCar::model).containsExactlyInAnyOrder("corolla", "rav4");
    }

    private static FoundCar car(String id, String title, String model, int price, Instant createdAt) {
        return new FoundCar(id, title, model, "https://link/" + id, "https://img/" + id, price, createdAt);    }
}
